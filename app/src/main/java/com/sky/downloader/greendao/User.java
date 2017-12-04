package com.sky.downloader.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by tonycheng on 2017/1/13.
 */
@Entity(nameInDb = "Table_User")
public class User {
    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    private Long id;
    @Property(nameInDb = "name")
    private String name;
    @Property(nameInDb = "age")
    private int age;
    @Property(nameInDb = "sex")
    private String sex;
    @Generated(hash = 689493095)
    public User(Long id, String name, int age, String sex) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.sex = sex;
    }
    @Generated(hash = 586692638)
    public User() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAge() {
        return this.age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public String getSex() {
        return this.sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
}
