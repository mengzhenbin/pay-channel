package com.dream.pay.channel.access.impl.repair;

import java.util.Date;

import com.dream.pay.channel.access.dto.PayQueryRepDTO;
import com.dream.pay.channel.access.dto.PayQueryReqDTO;
import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.access.repair.GatePayQueryFacade;
import com.dream.pay.channel.service.context.Channel;
import com.dream.pay.channel.service.exception.BaseException;
import com.youzan.platform.util.lang.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import lombok.extern.slf4j.Slf4j;

/**
 * 支付渠道服务
 * <p>
 * 网关支付
 *
 * @author mengzhenbin
 */
@Slf4j
public class GatePayQueryFacadeImpl implements GatePayQueryFacade {
    @Autowired
    private Channel channel;

    @Override
    public PayQueryRepDTO payQuery(PayQueryReqDTO payQueryReqDTO) {
        log.info("[支付查询][开始]-[{}]-[{}]-[{}]", payQueryReqDTO.getPayType().name(), payQueryReqDTO.getBizOrderNo(),
                DateUtil.currentDate());
        channel.select(payQueryReqDTO.getPayType());
        PayQueryRepDTO payQueryRepDTO = new PayQueryRepDTO();
        try {
            payQueryRepDTO = channel.getGateWayService().payQuery(payQueryReqDTO);
            log.info("[支付查询][结束]-[{}]-[{}]-[{}]-[{}]", payQueryRepDTO.getBizOrderNo(), payQueryRepDTO.getTradeStatus(),
                    payQueryRepDTO.getChlRtnMsg(), payQueryRepDTO.getChlRepDateTime());
        } catch (BaseException e) {
            payQueryRepDTO.setTradeStatus(TradeStatus.UNKNOW);
            payQueryRepDTO.setChlRtnCode(e.getErrorCode());
            payQueryRepDTO.setChlRtnMsg(e.getErrorMsg());
            payQueryRepDTO.setBizOrderNo(payQueryReqDTO.getBizOrderNo());
            log.error("[支付查询][发生异常]-[{}]-[{}]", payQueryReqDTO.getPayType().name(), payQueryReqDTO.getBizOrderNo(), e);
        }
        return payQueryRepDTO;
    }

}
