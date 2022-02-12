package org.server.remoteclass.controller;

import io.swagger.annotations.ApiOperation;
import org.server.remoteclass.dto.coupon.CouponDto;
import org.server.remoteclass.exception.IdNotExistException;
import org.server.remoteclass.service.CouponService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService){
        this.couponService = couponService;
    }

    //관리자 권한이므로 CouponDto로 모든 정보를 보여주게끔 한다.
    @ApiOperation(value = "전체 쿠폰 조회", notes = "현재까지 생성된 모든 쿠폰을 조회할 수 있다.")
    @GetMapping
    public ResponseEntity<List<CouponDto>> getAllCoupons(){
        return ResponseEntity.status(HttpStatus.OK).body(couponService.getAllCoupons());
    }

    //쿠폰 번호로 쿠폰 검색(관리자 권한)
    @ApiOperation(value = "쿠폰 번호로 쿠폰 조회", notes = "쿠폰 번호에 해당하는 쿠폰을 조회한다.")
    @GetMapping("/{couponId}")
    public ResponseEntity<CouponDto> getCoupon(@PathVariable("couponId") Long couponId){
        return ResponseEntity.status(HttpStatus.OK).body(couponService.getCouponByCouponId(couponId));
    }

    //쿠폰 생성(관리자 권한)
    @ApiOperation(value = "쿠폰 생성", notes = "새로운 쿠폰을 생성할 수 있다.")
    @PostMapping
    public ResponseEntity<CouponDto> createCoupon(@RequestBody @Valid CouponDto couponDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(couponService.createCoupon(couponDto));
    }

    //쿠폰 비활성화(관리자 권한)
    @ApiOperation(value = "쿠폰 비활성화", notes = "더 이상 쿠폰을 발급받을 수 없게 쿠폰을 비활성화 한다.")
    @PutMapping("/deactivate/{couponId}")
    public ResponseEntity<CouponDto> createCoupon(@PathVariable("couponId") Long couponId) throws IdNotExistException {
        return ResponseEntity.status(HttpStatus.OK).body(couponService.deactivateCoupon(couponId));
    }

    @ApiOperation(value = "쿠폰 삭제", notes = "쿠폰 목록에서 쿠폰을 삭제한다.")
    @DeleteMapping("/{couponId}")
    public ResponseEntity<CouponDto> deleteCoupon(@PathVariable("couponId") Long couponId) throws IdNotExistException {
        return ResponseEntity.status(HttpStatus.OK).body(couponService.deleteCoupon(couponId));
    }

}
