package vincent.example.jsonmappingdemo.model;

import java.util.ArrayList;

/**
 * Created by vincent on 11/1/2018.
 */

public class Followers {

    public ArrayList<Users> users;

    public class Users {
        public int id;
        public String name;
    }
}
