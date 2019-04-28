package com.jianke.life.busibs.mgr;

import com.jianke.life.busi.common.enums.MgrErrorCodeEnum;
import com.jianke.life.busi.common.exception.MgrBusinessException;
import com.jianke.life.busi.common.idc.service.FileUploadService;
import com.jianke.life.busi.utils.FileUploadUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author sutf
 * @ClassName: MgrUploadFileHelper
 * @Description: 后台上传文件相关
 * @date 2016-8-9 下午3:15:16
 * @history
 */
@Service
public class MgrUploadFileHelper {

    @Autowired
    private FileUploadService fileUploadService;

    /**
     * @param adSiteId 广告位id 存在该值表示当前上传的为广告图
     * @param imgFile
     * @param savaPath
     * @param idcPath
     * @return
     * @Description:
     */
    public String mgrUploadImageFile(Long adSiteId, MultipartFile imgFile, String savaPath, String idcPath) {

        String imgUrl = null;
        if (imgFile != null) {

            // 保存图片
            if (!StringUtils.isBlank(imgFile.getOriginalFilename())) {

                HashMap<String, Object> file = FileUploadUtil.getFile(imgFile, savaPath, "image");
                if (file.get("error") != null) {// 文件类型错误
                    throw new MgrBusinessException(MgrErrorCodeEnum.ERROR_DEFAULT.getCode(), "上传图片类型错误");
                } else {
                    File file1 = (File) file.get("sucess");

//                    if (adSiteId != null) { // 存在广告位id，进行大小校验
//                        checkImageSize(adSiteId, file1);
//                    }

                    String fileName = file1.getName();
                    int suffixIndex = fileName.lastIndexOf('.');
                    String suffix = fileName.substring(suffixIndex + 1);
                    String fileNameSave = "";// 保存至oss的文件名
                    String fileNameDownload = "";// 下载时的文件名

                    String result = fileUploadService.uploadFile(idcPath, savaPath + file1.getName(), fileNameSave, fileNameDownload, suffix);
                    if (result == "") {
                        throw new MgrBusinessException(MgrErrorCodeEnum.ERROR_DEFAULT.getCode(), "上传图片出错");
                    } else {
                        FileUploadUtil.deleteLocalFile(savaPath + file1.getName());
                        imgUrl = result;
                    }

                }
            }
        }

        return imgUrl;
    }

    /**
     * @param imageWidth
     * @param imageHeight
     * @param file1
     */
    private void checkImageSize(Integer imageWidth, Integer imageHeight, File file1) {
        try {

            BufferedImage sourceImg = javax.imageio.ImageIO.read(file1);
            if ((imageWidth != null && imageWidth != sourceImg.getWidth()) || (imageHeight != null && imageHeight != sourceImg.getHeight())) {
                throw new MgrBusinessException(MgrErrorCodeEnum.ERROR_DEFAULT.getCode(), "上传图片宽高错误,宽应为" + imageWidth + ",高应为" + imageHeight);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
