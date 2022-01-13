package com.changqing.gov.file.api.fallback;

import com.changqing.gov.base.R;
import com.changqing.gov.file.api.AttachmentApi;
import com.changqing.gov.file.dto.AttachmentDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 熔断
 *
 * @author changqing
 * @date 2019/07/25
 */
@Component
public class AttachmentApiFallback implements AttachmentApi {
    @Override
    public R<AttachmentDTO> upload(MultipartFile file, Boolean isSingle, Long id, String bizId, String bizType) {
        return R.timeout();
    }
}
