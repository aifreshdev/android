package vincent.example.jsonmappingdemo.model;

import java.util.ArrayList;

/**
 * Created by vincent on 11/1/2018.
 */

public class Contact {

    public int status;
    public List list;

    public class List{
        public ArrayList<FriendList> friendList;
        public ArrayList<FamilyList> familyList;
    }

    public class FriendList{
        public ObjectModel name;
        public ObjectModel phoneTelNo;
    }

    public class FamilyList{
        public ObjectModel name;
        public ObjectModel phoneTelNo;
    }
}
