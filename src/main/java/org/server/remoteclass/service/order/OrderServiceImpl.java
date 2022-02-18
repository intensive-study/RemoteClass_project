package org.server.remoteclass.service.order;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.server.remoteclass.constant.Authority;
import org.server.remoteclass.constant.OrderStatus;

import org.server.remoteclass.constant.Payment;
import org.server.remoteclass.dto.order.*;
import org.server.remoteclass.entity.*;
import org.server.remoteclass.exception.ForbiddenException;

import org.server.remoteclass.exception.IdNotExistException;
import org.server.remoteclass.exception.ResultCode;
import org.server.remoteclass.jpa.*;
import org.server.remoteclass.service.order.OrderService;
import org.server.remoteclass.util.BeanConfiguration;
import org.server.remoteclass.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final LectureRepository lectureRepository;
    private final OrderRepository orderRepository;
    private final OrderLectureRepository orderLectureRepository;
    private final CouponRepository couponRepository;
    private final IssuedCouponRepository issuedCouponRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public OrderServiceImpl(UserRepository userRepository, LectureRepository lectureRepository,
                            OrderRepository orderRepository, OrderLectureRepository orderLectureRepository,
                            CouponRepository couponRepository, IssuedCouponRepository issuedCouponRepository, BeanConfiguration beanConfiguration){
        this.userRepository = userRepository;
        this.lectureRepository = lectureRepository;
        this.orderRepository = orderRepository;
        this.orderLectureRepository = orderLectureRepository;
        this.couponRepository = couponRepository;
        this.issuedCouponRepository = issuedCouponRepository;
        this.modelMapper = beanConfiguration.modelMapper();
    }

    @Override
    @Transactional
    public Long createOrder(RequestOrderDto requestOrderDto) throws IdNotExistException, ForbiddenException {
        User user = SecurityUtil.getCurrentUserEmail()
                .flatMap(userRepository::findByEmail)
                .orElseThrow(() -> new IdNotExistException("존재하지 않는 사용자", ResultCode.ID_NOT_EXIST));
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setPayment(requestOrderDto.getPayment());
        if(requestOrderDto.getPayment() == Payment.BANK_ACCOUNT){
            order.setBank(requestOrderDto.getBank());
            order.setAccount(requestOrderDto.getAccount());
        }
        IssuedCoupon issuedCoupon;
        if(requestOrderDto.getIssuedCouponId() == null){ //쿠폰값 입력 안했을때
            order.setIssuedCoupon(null);
        }
        else{  //쿠폰값 입력했을때
            issuedCoupon= issuedCouponRepository.findByIssuedCouponId(requestOrderDto.getIssuedCouponId());
            if(issuedCoupon==null){  //없는 쿠폰 입력했을 때
                throw new IdNotExistException("존재하지 않는 쿠폰입니다", ResultCode.ID_NOT_EXIST);
            }
            // 이미 사용한 쿠폰 입력했을 때 or 유효하지 않는 쿠폰 입력했을 때
            if(issuedCoupon.isCouponUsed() || LocalDateTime.now().isAfter(issuedCoupon.getCouponValidDate())){
                throw new ForbiddenException("이미 사용했거나 유효하지 않은 쿠폰입니다", ResultCode.FORBIDDEN);
            }
            order.setIssuedCoupon(issuedCoupon);
        }
        orderRepository.save(order);

        List<OrderLecture> orderLectureList = order.getOrderLectures();
        for(RequestOrderLectureDto requestOrderLectureDto : requestOrderDto.getOrderLectures()) {
            OrderLecture orderLecture = new OrderLecture();
            Lecture lecture = lectureRepository.findById(requestOrderLectureDto.getLectureId()).orElseThrow(() -> new IdNotExistException("존재하지 않는 강의", ResultCode.ID_NOT_EXIST));
            orderLecture.setLecture(lecture);
            orderLecture.setOrder(order);
            orderLectureList.add(orderLectureRepository.save(orderLecture));
        }
        order.setOriginalPrice(orderRepository.findSumOrderByOrderId(order.getOrderId()));
        orderRepository.save(order);

        return order.getOrderId();
    }

    //    주문 취소
    @Override
    @Transactional
    public void cancelOrder(Long orderId) throws IdNotExistException, ForbiddenException {
        User user = SecurityUtil.getCurrentUserEmail()
                .flatMap(userRepository::findByEmail)
                .orElseThrow(() -> new IdNotExistException("존재하지 않는 사용자", ResultCode.ID_NOT_EXIST));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IdNotExistException("존재하지 않는 주문", ResultCode.ID_NOT_EXIST));
        if(user.getUserId() != order.getUser().getUserId()){
            throw new ForbiddenException("취소 권한이 없습니다", ResultCode.FORBIDDEN);
        }
        order.setOrderStatus(OrderStatus.CANCEL);
    }

    //사용자 본인것만 조회
    @Override
    public List<ResponseOrderDto> getMyOrdersByUserId() throws IdNotExistException {
        User user = SecurityUtil.getCurrentUserEmail()
                .flatMap(userRepository::findByEmail)
                .orElseThrow(() -> new IdNotExistException("존재하지 않는 사용자", ResultCode.ID_NOT_EXIST));
        List<Order> orders = orderRepository.findByUser_UserIdOrderByOrderDateDesc(user.getUserId());
        return orders.stream().map(ResponseOrderDto::new).collect(Collectors.toList());
    }

    //관리자 전체 조회
    @Override
    public List<ResponseOrderByAdminDto> getAllOrdersByAdmin() throws IdNotExistException, ForbiddenException {
        User user = SecurityUtil.getCurrentUserEmail()
                .flatMap(userRepository::findByEmail)
                .orElseThrow(() -> new IdNotExistException("존재하지 않는 사용자", ResultCode.ID_NOT_EXIST));
        List<Order> orders;
        if(user.getAuthority() == Authority.ROLE_ADMIN){
            orders = orderRepository.findByOrderByOrderDateDesc();
        }
        else{
            throw new ForbiddenException("접근 권한 없습니다", ResultCode.FORBIDDEN);
        }
        return orders.stream().map(ResponseOrderByAdminDto::new).collect(Collectors.toList());
    }

    //관리자가 사용자별로 조회
    @Override
    public List<ResponseOrderByAdminDto> getOrderByUserIdByAdmin(Long userId) throws IdNotExistException, ForbiddenException {
        User user = SecurityUtil.getCurrentUserEmail()
                .flatMap(userRepository::findByEmail)
                .orElseThrow(() -> new IdNotExistException("존재하지 않는 사용자", ResultCode.ID_NOT_EXIST));
        List<Order> orders;
        if(user.getAuthority() == Authority.ROLE_ADMIN){
            orders = orderRepository.findByUser_UserIdOrderByOrderDateDesc(userId);
        }
        else{
            throw new ForbiddenException("접근 권한 없습니다", ResultCode.FORBIDDEN);
        }
        return orders.stream().map(ResponseOrderByAdminDto::new).collect(Collectors.toList());
    }

    //관리자가 주문번호별로 조회
    @Override
    @Transactional(readOnly = true)
    public ResponseOrderByAdminDto getOrderByOrderIdByAdmin(Long orderId) throws IdNotExistException, ForbiddenException {
        User user = SecurityUtil.getCurrentUserEmail()
                .flatMap(userRepository::findByEmail)
                .orElseThrow(() -> new IdNotExistException("존재하지 않는 사용자", ResultCode.ID_NOT_EXIST));
        Order order;
        if(user.getAuthority() == Authority.ROLE_ADMIN){
            order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IdNotExistException("존재하지 않는 주문", ResultCode.ID_NOT_EXIST));
        }
        else{
            throw new ForbiddenException("접근 권한 없습니다", ResultCode.FORBIDDEN);
        }
        return new ResponseOrderByAdminDto(order);
    }
}
