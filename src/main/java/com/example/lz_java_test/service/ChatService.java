// package com.example.lz_java_test.service;

// import com.example.lz_java_test.model.Message;
// import com.example.lz_java_test.repository.MessageRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import java.util.List;

// @Service
// public class ChatService {

// @Autowired
// private MessageRepository messageRepository;

// public void saveMessage(Message message) {
// messageRepository.save(message);
// }

// public List<Message> getRecentMessages(int count) {
// return messageRepository.findTopNByOrderByTimestampDesc(count);
// }
// }
