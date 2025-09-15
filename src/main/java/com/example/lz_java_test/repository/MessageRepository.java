// package com.example.lz_java_test.repository;

// import com.example.lz_java_test.model.Message;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.stereotype.Repository;

// import java.util.List;

// @Repository
// public interface MessageRepository extends JpaRepository<Message, Long> {

// /**
// * 查询最新的 N 条消息
// */
// @Query("SELECT m FROM Message m ORDER BY m.timestamp DESC")
// List<Message> findTopNByOrderByTimestampDesc(int n);
// }
