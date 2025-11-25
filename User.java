import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class User {
    private Long id;
    private String email;
    private String password;
    private String name;
    private Integer age;
    private String major;
    private String bio;
    private boolean verified = false;  //whether the email and authentication is completed
    private Instant createdAt = Instant.now();

    private Set<String> interests = new HashSet<>();
    private Set<String> tags = new HashSet<>();

    public User() {}

    //============ Getter and Setters ===============
    public Long getId()
    {
        return id;
    }
    public void setId(Long id)
    {
        this.id = id;
    }
    public String getEmail()
    {
        return email;
    }
    public void setEmail(String email)
    {
        this.email = email;
    }
    public String getPassword()
    {
        return password;
    }
    public void setPassword()
    {
        this.password = password;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String Name)
    {
        this.name = name;
    }
    public Integer getAge()
    {
        return age;
    }
    public void setAge(Integer age)
    {
        this.age = age;
    }
    public String getMajor()
    {
        return major;
    }
    public void setMajor(String major)
    {
        this.major = major;
    }
    public String getBio()
    {
        return bio;
    }
    public void setBio(String bio)
    {
        this.bio = bio;
    }
    public Set<String> getInterests()
    {
        return interests;
    }
    public void setInterests(Set<String> interests)
    {
        this.interests = interests;
    }
    public Set<String> getTags()
    {
        return tags;
    }
    public void setTags(Set<String> tags)
    {
        this.tags = tags;
    }
    public boolean isVerified()
    {
        return verified;
    }
    public void setVerified(boolean verified)
    {
        this.verified = verified;
    }

    public Instant getCreatedAt()
    {
        return createdAt;
    }
    public void setCreatedAt(Instant createdAt)
    {
        this.createdAt = createdAt;
    }








    
    


}