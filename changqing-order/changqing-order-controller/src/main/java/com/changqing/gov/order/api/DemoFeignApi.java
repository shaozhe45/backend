package com.changqing.gov.order.api;

import com.changqing.gov.base.R;
import com.changqing.gov.order.dto.RestTestDTO;
import com.changqing.gov.order.entity.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 测试日期类型API接口
 *
 * @author changqing
 * @date 2019/07/24
 */
@FeignClient(name = "${changqing.feign.demo-server:changqing-demo-server}", path = "/restTemplate")
public interface DemoFeignApi {
    @GetMapping("/get")
    R<Order> get(@RequestParam("key") String key,
                 @RequestParam(value = "date", required = false) Date date,
                 @RequestParam(value = "ldt", required = false) LocalDateTime ldt);

    @PostMapping("/post")
    R<RestTestDTO> post(@RequestParam("key") String key);

    @PostMapping("/post2")
    R<RestTestDTO> post(@RequestBody RestTestDTO order);


    @PostMapping("/fallback")
    R<RestTestDTO> fallback(@RequestParam("key") String key);

    @PostMapping("/fallback2")
    RestTestDTO fallback2(@RequestParam("key") String key);


    /**
     * 通过feign-form 实现文件 跨服务上传
     *
     * @param file
     * @param id
     * @param bizId
     * @param bizType
     * @return
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    R<String> upload(
            @RequestPart(value = "file") MultipartFile file,
            @RequestParam(value = "isSingle", required = false, defaultValue = "false") Boolean isSingle,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "bizId", required = false) String bizId,
            @RequestParam(value = "bizType", required = false) String bizType);
}
