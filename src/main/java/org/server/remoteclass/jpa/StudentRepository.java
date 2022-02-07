package org.server.remoteclass.jpa;

import org.server.remoteclass.entity.Lecture;
import org.server.remoteclass.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    // 강좌별 수강생 전체 리스트
     List<Student> findByLectureId(Long lectureId);
    //수강생 별 수강강좌 리스트
    List<Student> findByUser_UserIdOrderByLecture_StartDateDesc(Long userId);

    //수강생 별 수강강좌 리스트
//    List<Lecture> findByStudentId(Long userId);

}