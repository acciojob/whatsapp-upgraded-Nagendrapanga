package com.driver;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class WhatsappRepository {

    private HashMap<String, User> userMap;

    private HashMap<Group, List<User>> groupMap;
    private HashMap<Integer, Message> messages;

    private HashMap<Group, List<Message>> groupMessage;
    private HashMap<User, List<Message>> userMessage;

    private int groupCount=0;
    private int messageCount=0;

    public WhatsappRepository() {
        this.userMap = new HashMap<>();
        this.groupMap = new HashMap<>();
        this.messages = new HashMap<>();
        this.groupMessage = new HashMap<>();
        this.userMessage = new HashMap<>();
    }

    public String createUser(String name, String mobile) throws Exception{

        User user = new User(mobile, name);
        userMap.put(mobile,user);
        return "SUCCESS";
    }
    public int createMessage(String content){
        messageCount++;
        Message message = new Message(messageCount, content);
        message.setTimestamp(new Date());
        messages.put(messageCount,message);
        return messageCount;
    }
    public Group createGroup(List<User> users){
        if(users.size()==2){
            Group group = new Group(users.get(1).getName(),2); //or get(0)
            groupMap.put(group,users);
            return group;
        }
        else{
            groupCount++;
            Group group = new Group("Group "+groupCount, users.size());
            groupMap.put(group, users);
            return group;
        }
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception{
        if(!groupMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        if(!groupMap.get(group).contains(sender)){
            throw new Exception("You are not allowed to send message");
        }

        if(!groupMessage.containsKey(group)){
            List<Message> messages1 = new ArrayList<>();
            messages1.add(message);
            groupMessage.put(group, messages1);
        }
        else{
            groupMessage.get(group).add(message);
        }

        if(!userMessage.containsKey(sender)){
            List<Message> messages1 = new ArrayList<>();
            messages1.add(message);
            userMessage.put(sender, messages1);
        }
        else{
            userMessage.get(sender).add(message);
        }
        return groupMessage.get(group).size();
    }

    public int removeUser(User user) throws Exception{
        boolean userExist = false;
        boolean isAdmin = false;
        Group groupName = null;
        for(Group group:groupMap.keySet()){
            int num = 0;
            for(User user1:groupMap.get(group)){
                num++;
                if(user1.equals(user)){
                    if(num==1){
                        isAdmin = true;
                    }
                    userExist = true;
                    groupName = group;
                    break;
                }
            }
            if(userExist){
                break;
            }
        }
        if(!userExist){
            throw new Exception("User not found");
        }
        if(isAdmin){
            throw new Exception("Cannot remove admin");
        }

        List<Message> userMessages=userMessage.get(user);

        for(Message message: userMessages){
            messages.remove(message.getId());
            groupMessage.get(groupName).remove(message);
        }



        groupMap.get(groupName).remove(user);

        userMessages.remove(user);

        return groupMap.get(groupName).size()+groupMessage.get(groupName).size()+messages.size();

    }
    public String changeAdmin(User approver, User user, Group group)throws Exception{

        if(!groupMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        if(!approver.equals(groupMap.get(group).get(0))){
            throw new Exception("Approver does not have rights");
        }
        boolean checker = false;
        for(User user1 : groupMap.get(group)){
            if(user1.equals(user)){
                checker = true;
            }
        }

        if(!checker){
            throw new Exception("User is not a participant");
        }

        User oldAdmin = groupMap.get(group).get(0);
        groupMap.get(group).set(0, user);
        groupMap.get(group).add(oldAdmin);

        return "SUCCESS";
    }
    public String findMessage(Date start, Date end, int K) throws Exception{

        boolean latest = false;
        int k = 0;
        String message = null;
        for(int i=messages.size()-1; i>=0; i--){
            Message messages1 = messages.get(i);
            if(messages1.getTimestamp().compareTo(start)>0 && messages1.getTimestamp().compareTo(end)<0){
                k++;
                if(k==1){
                    latest = true;
                }
                if(latest) {
                    message = messages1.getContent();
                    latest = false;
                }
            }
        }
        if(k<K){
            throw new Exception("K is greater than the number of messages");
        }
        return message;
    }
}
