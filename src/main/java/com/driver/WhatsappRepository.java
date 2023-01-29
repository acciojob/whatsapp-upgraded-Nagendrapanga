package com.driver;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class WhatsappRepository {

    private int groupCount=0;

    private int messageCount=0;

    HashMap<String,User> userMap=new HashMap<>();

    HashMap<Group,List<User>> groupMap=new HashMap<>();

    HashMap<Group,List<Message>> messagesInGroup=new HashMap<>();

    List<Message> messagesList = new ArrayList<>();

    HashMap<User,List<Message>> userMessagesList = new HashMap<>();


    public void createUser(String name, String mobile)throws Exception{

        if(userMap.containsKey(mobile)){
            throw new Exception("User already exists");
        }
        User user=new User(name, mobile);
        userMap.put(mobile,user);
    }

    public Group createGroup(List<User> users) {

        if(users.size()==2){
            Group group = new Group(users.get(1).getName(),2);
            groupMap.put(group,users);
            return group;
        }
        Group group = new Group("Group "+ ++groupCount,users.size());
        groupMap.put(group,users);
        return group;

    }

    public int createMessage(String content) {

        Message message = new Message(++messageCount,content);
        message.setTimestamp(new Date());
        messagesList.add(message);
        return messageCount;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception{

        if(!groupMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        boolean check = false;
        for(User user:groupMap.get(group)){
            if(user.equals(sender)){
                check = true;
                break;
            }
        }

        if(!check){
            throw new Exception("You are not allowed to send message");
        }

        if(messagesInGroup.containsKey(group)){
            messagesInGroup.get(group).add(message);
        }
        else {
            List<Message> messages = new ArrayList<>();
            messages.add(message);
            messagesInGroup.put(group,messages);
        }

        if(userMessagesList.containsKey(sender)){
            userMessagesList.get(sender).add(message);
        }
        else {
            List<Message> messages = new ArrayList<>();
            messages.add(message);
            userMessagesList.put(sender,messages);
        }

        return messagesInGroup.get(group).size();
    }

    public void changeAdmin(User approver, User user, Group group) throws Exception{


        if(!groupMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }

        User pAdmin = groupMap.get(group).get(0);
        if(!approver.equals(pAdmin)){
            throw new Exception("Approver does not have rights");
        }

        boolean check = false;
        for(User user1:groupMap.get(group)){
            if(user1.equals(user)){
                check = true;
            }
        }

        if(!check){
            throw new Exception("User is not a participant");
        }

        User newAdmin = null;
        Iterator<User> userIterator = groupMap.get(group).iterator();

        while(userIterator.hasNext()){
            User u = userIterator.next();
            if(u.equals(user)){
                newAdmin = u;
                userIterator.remove();
            }
        }

        groupMap.get(group).add(0,newAdmin);

    }

    public int removeUser(User user)throws Exception {


        boolean check = false;
        Group group1 = null;
        for(Group group:groupMap.keySet()){
            for(User user1:groupMap.get(group)){
                if(user1.equals(user)){
                    check=true;
                    group1=group;
                    break;
                }
            }
        }
        if(!check){
            throw new Exception("User not found");
        }

        if(groupMap.get(group1).get(0).equals(user)){
            throw new Exception("Cannot remove admin");
        }

        List<Message> userMessages=userMessagesList.get(user);

        for(Group group:messagesInGroup.keySet()){
            for(Message message:messagesInGroup.get(group)){
                if(userMessages.contains(message)){
                    messagesInGroup.get(group).remove(message);
                }
            }
        }

        for(Message message:messagesList){
            if(userMessages.contains(message)){
                messagesList.remove(message);
            }
        }
        groupMap.get(group1).remove(user);
        userMessagesList.remove(user);
        return groupMap.get(group1).size()+messagesInGroup.get(group1).size()+messagesList.size();

    }


}
