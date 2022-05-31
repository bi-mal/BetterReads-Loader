package com.example.betterreadsloader;

import com.example.betterreadsloader.Author.Author;
import com.example.betterreadsloader.Author.AuthorRepo;
import com.example.betterreadsloader.book.bookRepo;
import com.example.betterreadsloader.connection.DataStaxProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import javax.annotation.PostConstruct;
import com.example.betterreadsloader.book.Book;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxProperties.class)
public class BetterreadsLoaderApplication {
    @Autowired
    AuthorRepo authorRepo;

    @Autowired
    bookRepo bookRepo;

    @Value("${datadump.location.author}")
    private String authorDumpLocation;

    @Value("${datadump.location.works}")
    private String worksDumpLocaiton;

    public static void main(String[] args) {
        SpringApplication.run(BetterreadsLoaderApplication.class, args);
    }

    private void initAuthors()
    {
        var path= Paths.get(authorDumpLocation);
        try(var lines= Files.lines(path))
        {
            lines.forEach(line->{
                String jsonstring= line.substring(line.indexOf("{"));
                try {
                    JSONObject jsonobject=new JSONObject(jsonstring);

                    Author author=new Author();
                    author.setName(jsonobject.optString("name"));
                    author.setPersonal_name(jsonobject.optString("personal_name"));
                    author.setId(jsonobject.optString("key").replace("/authors/",""));

                    System.out.println("saving author"+ author.getName());
                    authorRepo.save(author);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }catch (IOException e) {
            e.printStackTrace()  ;
        }
    }
    private void initWorks()
    {
        var path= Paths.get(worksDumpLocaiton);
        var dateFormat=DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        try(var lines= Files.lines(path))
        {
            lines.forEach(line-> {
                String jsonstring = line.substring(line.indexOf("{"));
                try {
                    JSONObject jsonobject=new JSONObject(jsonstring);

                    Book book=new Book();
                    book.setId(jsonobject.optString("key").replace("/works/",""));
                    book.setName(jsonobject.optString("title"));

                    var descriptionObject=jsonobject.optJSONObject("description");
                    if(descriptionObject!=null) {
                        book.setDescription(descriptionObject.optString("value"));
                    }

                    var publishedOb=jsonobject.optJSONObject("created");
                    if(publishedOb!=null) {
                        String datestr = publishedOb.getString("value");
                        book.setPublished_date(LocalDate.parse(datestr,dateFormat));
                    }

                    var coverArray=jsonobject.optJSONArray("covers");
                    if(coverArray!=null) {
                        List<String> coverIds=new ArrayList<>();
                        for(int i=0;i<coverArray.length();i++)
                            coverIds.add(coverArray.getString(i));
                        book.setCoverIds(coverIds);
                    }

                    var authorArr=jsonobject.optJSONArray("authors");
                    if(authorArr!=null){
                        List<String> authorIds=new ArrayList<>();
                        for(int i=0;i<authorArr.length();i++)
                        {
                            var authorId=authorArr
                                    .getJSONObject(i)
                                    .getJSONObject("author")
                                    .getString("key")
                                    .replace("/authors/","");
                            authorIds.add(authorId);
                        }
                        book.setAuthorIds(authorIds);
                        List<String> authorNames=authorIds.stream().map(id->authorRepo.findById(id))
                                .map(optionalAuthor->{
                                        if(!optionalAuthor.isPresent()) return "unknown Author";
                                        else
                                            return optionalAuthor.get().getName();
                                }).collect(Collectors.toList());
                        book.setAuthorNames(authorNames);
                    }
                    System.out.println("saving book  "+book.getName());
                    bookRepo.save(book);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }catch (IOException e) {
            e.printStackTrace()  ;
        }
    }

    @PostConstruct
    public void start()
    {
//        initAuthors();
        initWorks();
    }



    @Bean
    public CqlSessionBuilderCustomizer SessionBuilderCustomizer(DataStaxProperties astraProperties) {
        Path bundle = astraProperties.getSecureConnectBundle().toPath();
        return builder -> builder.withCloudSecureConnectBundle(bundle);
    }
}
