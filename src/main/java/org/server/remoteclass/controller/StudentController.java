package org.server.remoteclass.controller;

import org.server.remoteclass.dto.StudentDto;
import org.server.remoteclass.dto.StudentFormDto;
import org.server.remoteclass.entity.Student;
import org.server.remoteclass.exception.IdNotExistException;
import org.server.remoteclass.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService){
        this.studentService = studentService;
    }

    //수강 신청 학생 권한
    @PostMapping()
    public ResponseEntity applyLecture(@RequestBody @Valid StudentFormDto studentFormDto) throws IdNotExistException {
        Student student = studentService.applyLecture(studentFormDto);
        StudentDto studentDto = new StudentDto(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(studentDto);
    }

    //수강하는 강좌 전체 조회
    @GetMapping("/list")
    public List<StudentDto> getAllLectureByUserId() throws IdNotExistException {
        return this.studentService.getLecturesByUserId().stream()
                .map(StudentDto::new).collect(Collectors.toList());
    }

    //수강생 전체 조회 /강의자 권한
    @GetMapping("{lectureId}/list")
    public List<StudentDto> getStudentsByLectureId(@PathVariable("lectureId") Long lectureId) throws IdNotExistException{
        return this.studentService.getStudentsByLectureId(lectureId).stream()
                .map(StudentDto::new).collect(Collectors.toList());
    }
}