package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
@Repository
public class WhatsappRepository {

    private HashMap<String,User> userMap;
    private HashMap<String,Group> groupMap;
    private HashMap<Group, List<User>> userGroupMap;

    private HashMap<Integer,Message> messageMap;
    private HashMap<Group,List<Message>> groupMessageMap;

    public WhatsappRepository() {
        this.userMap = new HashMap<String, User>();
        this.groupMap = new HashMap<String, Group>();
        this.userGroupMap = new HashMap<Group, List<User>>();
        this.messageMap = new HashMap<Integer, Message>();
        this.groupMessageMap = new HashMap<Group, List<Message>>();
    }
    public String createUser(String name, String mobile) throws Exception {
        User user = new User();
        user.setName(name);
        user.setMobile(mobile);
        if(userMap!=null &&userMap.containsKey(mobile)){
            throw new Exception("User already exists");
        }
        userMap.put(mobile,user);
        return "SUCCESS";
    }

    public Group createGroup(List<User> users) {

        Group group = new Group();

        group.setNumberOfParticipants(users.size());

        if(users.size()<=2){
            group.setName(users.get(2).getName());
        }
        else {
            group.setName("Group" +" "+ users.size());
        }
        groupMap.put(group.getName(),group);
        List<User> userList = new ArrayList<>();
        userGroupMap.put(group, userList);
        List<Message> messageList=new ArrayList<>();
        groupMessageMap.put(group,messageList);
        return group;

    }

    public int createMessage(String content) {

        Message message=new Message();
        message.setContent(content);
        message.setId(messageMap.size());

        messageMap.put(message.getId(),message);

        return message.getId();
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception{

        if(!groupMap.containsKey(group.getName())){
            throw  new Exception("Group does not exist");
        }
        List<User> users= userGroupMap.get(group);

        boolean flag=false;
        for(User user:users){
            if(sender==user){
                flag=true;
            }
        }
        if(flag==false){
            throw new Exception("You are not allowed to send message");
        }
        List<Message> messages = groupMessageMap.get(group);
        if(messages == null){
            messages = new ArrayList<>();
        }
        messages.add(message);

        groupMessageMap.put(group,messages);

        return messages.size()-1;
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception{

        if(!groupMap.containsKey(group.getName())){

            throw new Exception("Group does not exist");
        }

        List<User> users= userGroupMap.get(group);


        if(users!=null && approver!=users.get(0)){

            throw new Exception("Approver does not have rights");
        }

        boolean flag=false;
        for(User user1:users){

            if(user1==user){

                flag=true;
            }
        }
        if(flag==false){

            throw new Exception("User is not a participant");
        }

        User user1= users.get(0);

        users.add(users.size()-1,user1);

        users.remove(0);

        return "SUCCESS";
    }

    public int removeUser(User user)throws Exception {

        boolean flag = false;
        boolean isAdmin = false;
        Group group = new Group();
        for(Group group1: userGroupMap.keySet()){
            List<User> userList= userGroupMap.get(group);
            for(User user1: userList){
                if(user1==user){
                    flag=true;
                    if(userList.get(0)==user){
                        isAdmin = true;
                    }
                    group = group1;
                    break;
                }
            }
        }
        if(flag==false){
            throw new Exception("User not found");
        }
        if(isAdmin){
            throw new Exception("Cannot remove admin");
        }
        if(!isAdmin){

            List<User> users = userGroupMap.get(group);
            users.remove(user);
            userGroupMap.put(group,users);
            userMap.remove(user);
        }
        return userGroupMap.get(group).size();

    }

    public HashMap<String, User> getUserMap() {
        return userMap;
    }

    public void setUserMap(HashMap<String, User> userMap) {
        this.userMap = userMap;
    }

    public HashMap<String, Group> getGroupMap() {
        return groupMap;
    }

    public void setGroupMap(HashMap<String, Group> groupMap) {
        this.groupMap = groupMap;
    }

    public HashMap<Group, List<User>> getUserGroupMap() {
        return userGroupMap;
    }

    public void setUserGroupMap(HashMap<Group, List<User>> userGroupMap) {
        this.userGroupMap = userGroupMap;
    }

    public HashMap<Integer, Message> getMessageMap() {
        return messageMap;
    }

    public void setMessageMap(HashMap<Integer, Message> messageMap) {
        this.messageMap = messageMap;
    }

    public HashMap<Group, List<Message>> getGroupMessageMap() {
        return groupMessageMap;
    }

    public void setGroupMessageMap(HashMap<Group, List<Message>> groupMessageMap) {
        this.groupMessageMap = groupMessageMap;
    }
}
