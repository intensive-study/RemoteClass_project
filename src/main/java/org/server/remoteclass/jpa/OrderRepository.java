package org.server.remoteclass.jpa;

import org.server.remoteclass.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 회원이 주문한 주문 목록 최신순으로
    @Query("select o from Order o " +
            "join fetch o.issuedCoupon i " +
            "join fetch i.coupon c " +
            "where o.user.userId=:userId " +
            "order by o.orderDate desc ")
    List<Order> findByUser_UserIdOrderByOrderDateDesc(Long userId);

    List<Order> findByOrderByOrderDateDesc();

    @Query("select sum(l.price) from OrderLecture o " +
            "join o.lecture l " +
            "where o.order.orderId=:orderId")
    Integer findSumOrderByOrderId(Long orderId);

    List<Order> findByOrderLectures_Lecture_LectureId(Long lectureId);

}
