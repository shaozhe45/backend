package com.changqing.gov.file.strategy.impl;

import com.changqing.gov.exception.BizException;
import com.changqing.gov.file.domain.FileDeleteDO;
import com.changqing.gov.file.entity.File;
import com.changqing.gov.file.enumeration.IconType;
import com.changqing.gov.file.properties.FileServerProperties;
import com.changqing.gov.file.strategy.FileStrategy;
import com.changqing.gov.file.utils.FileDataTypeUtil;
import com.changqing.gov.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static com.changqing.gov.exception.code.ExceptionCode.BASE_VALID_PARAM;


/**
 * 文件抽象策略 处理类
 *
 * @author changqing
 * @date 2019/06/17
 */
@Slf4j
public abstract class AbstractFileStrategy implements FileStrategy {

    private static final String FILE_SPLIT = ".";
    @Autowired
    protected FileServerProperties fileProperties;

    /**
     * 上传文件
     *
     * @param multipartFile
     * @return
     */
    @Override
    public File upload(MultipartFile multipartFile) {
        try {
            if (!multipartFile.getOriginalFilename().contains(FILE_SPLIT)) {
                throw BizException.wrap(BASE_VALID_PARAM.build("缺少后缀名"));
            }

            File file = File.builder()
                    .isDelete(false).submittedFileName(multipartFile.getOriginalFilename())
                    .contextType(multipartFile.getContentType())
                    .dataType(FileDataTypeUtil.getDataType(multipartFile.getContentType()))
                    .size(multipartFile.getSize())
                    .ext(FilenameUtils.getExtension(multipartFile.getOriginalFilename()))
                    .build();
            file.setIcon(IconType.getIcon(file.getExt()).getIcon());
            setDate(file);
            uploadFile(file, multipartFile);
            return file;
        } catch (Exception e) {
            log.error("e={}", e);
            throw BizException.wrap(BASE_VALID_PARAM.build("文件上传失败"));
        }
    }

    /**
     * 具体类型执行上传操作
     *
     * @param file
     * @param multipartFile
     * @throws Exception
     */
    protected abstract void uploadFile(File file, MultipartFile multipartFile) throws Exception;

    private void setDate(File file) {
        LocalDateTime now = LocalDateTime.now();
        file.setCreateMonth(DateUtils.formatAsYearMonthEn(now))
                .setCreateWeek(DateUtils.formatAsYearWeekEn(now))
                .setCreateDay(DateUtils.formatAsDateEn(now));
    }

    @Override
    public boolean delete(List<FileDeleteDO> list) {
        if (list.isEmpty()) {
            return true;
        }
        boolean flag = false;
        for (FileDeleteDO file : list) {
            try {
                delete(list, file);
                flag = true;
            } catch (Exception e) {
                log.error("删除文件失败", e);
            }
        }
        return flag;
    }

    /**
     * 具体执行删除方法， 无需处理异常
     *
     * @param list
     * @param file
     * @author changqing
     * @date 2019-05-07
     */
    protected abstract void delete(List<FileDeleteDO> list, FileDeleteDO file);

}
