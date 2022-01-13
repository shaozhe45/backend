package com.changqing.gov.file.strategy;


import com.changqing.gov.base.R;
import com.changqing.gov.file.dto.chunk.FileChunksMergeDTO;
import com.changqing.gov.file.entity.File;

/**
 * 文件分片处理策略类
 *
 * @author changqing
 * @date 2019/06/19
 */
public interface FileChunkStrategy {

    /**
     * 根据md5检测文件
     *
     * @param md5
     * @param folderId
     * @param accountId
     * @return
     */
    File md5Check(String md5, Long folderId, Long accountId);

    /**
     * 合并文件
     *
     * @param merge
     * @return
     */
    R<File> chunksMerge(FileChunksMergeDTO merge);
}
