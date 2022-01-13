package com.changqing.gov.file.domain;


import com.changqing.gov.file.entity.File;
import lombok.Data;

/**
 * 文件查询 DO
 *
 * @author changqing
 * @date 2019/05/07
 */
@Data
public class FileQueryDO extends File {
    private File parent;

}
