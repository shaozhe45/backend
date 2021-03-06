package com.changqing.gov.poi;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import cn.afterturn.easypoi.excel.entity.result.ExcelVerifyHandlerResult;
import cn.afterturn.easypoi.handler.inter.IExcelDataModel;
import cn.afterturn.easypoi.handler.inter.IExcelModel;
import cn.afterturn.easypoi.handler.inter.IExcelVerifyHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.changqing.gov.authority.dto.auth.UserExcelVO;
import com.changqing.gov.authority.entity.auth.User;
import com.changqing.gov.authority.entity.core.Org;
import com.changqing.gov.authority.entity.core.Station;
import com.changqing.gov.authority.enumeration.auth.Sex;
import com.changqing.gov.model.RemoteData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.poi.ss.usermodel.Workbook;
import org.hibernate.validator.constraints.Length;
import org.junit.Test;

import javax.validation.constraints.NotEmpty;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

public class PoiTest {
    public static final String OUTPUT_PATH = "/Users/tangyh/Downloads/testPoi/";

    private static List<User> USER_LIST = new ArrayList<>();
    private static List<UserExcelVO> USER_EXCEL_LIST = new ArrayList<>();

    static {
        for (int i = 0; i < 2; i++) {
            User user = User.builder()
                    .orgId(new RemoteData<>(Long.valueOf(i), Org.builder().label("org" + i).build()))
                    .stationId(new RemoteData<>(Long.valueOf(i), "stataiin" + i))
                    .account("??????" + i)
                    .name("??????" + i)
                    .mobile("15218869970")
                    .sex(i % 2 == 0 ? Sex.M : Sex.M)
                    .nation(new RemoteData<>("HELLO" + i))
                    .education(new RemoteData<>("xueli" + i, "??????"))
                    .workDescribe("????????????????????????????????????????????????????????????????????????????????????." + i)
                    .status(true)
                    .createTime(LocalDateTime.now())
                    .lastLoginTime(LocalDateTime.now())
                    .build();
            if (i == 0) {
                user.setSex(null);
            }
            if (i == 1) {
                user.setSex(Sex.N);
            }
            USER_LIST.add(user);
            UserExcelVO userEX = UserExcelVO.builder()
                    .account("??????" + i)
                    .name("??????" + i)
                    .mobile("15218869970")
                    .sex(i % 2 == 0 ? Sex.M : Sex.M)
                    .nation("??????" + i)
//                    .nation(new RemoteData<>("HELLO" + i, "??????" + i))
                    .workDescribe("????????????????????????????????????????????????????????????????????????????????????." + i)
                    .status(true)
                    .lastLoginTime(LocalDateTime.now())
                    .build();
            if (i == 0) {
                userEX.setSex(null);
            }
            if (i == 1) {
                userEX.setSex(Sex.N);
            }
            USER_EXCEL_LIST.add(userEX);
        }
    }


    @Test
    @SneakyThrows
    public void testExport() {
        ExportParams params = new ExportParams(null, "??????", ExcelType.XSSF);
        Workbook workbook = ExcelExportUtil.exportExcel(params, User.class, USER_EXCEL_LIST);
        File savefile = new File(OUTPUT_PATH);
        if (!savefile.exists()) {
            savefile.mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(OUTPUT_PATH + "User.xlsx");
        workbook.write(fos);
        fos.close();
    }

    @Test
    public void testGet() {
        User user = USER_LIST.get(0);

        System.out.println(user.getOrg());
    }


    @Test
    public void testImport() throws Exception {
        ImportParams params = new ImportParams();
        params.setTitleRows(0);
        params.setHeadRows(1);
        params.setNeedVerify(true);
        params.setVerifyHandler(new ExcelVerifyHandlerImpl());

//        List<Map<String, String>> list = ExcelImportUtil.importExcel(new File(OUTPUT_PATH + "??????????????????.xlsx"), Map.class, params);

        ExcelImportResult<Station> result = ExcelImportUtil.importExcelMore(
                new File(OUTPUT_PATH + "??????????????????.xlsx"),
//                new File(OUTPUT_PATH + "??????????????????.xlsx"),
                Station.class, params);

//        ExcelImportResult<Map<String, String>> result = ExcelImportUtil.importExcelMore(
//                new File(OUTPUT_PATH + "??????????????????.xlsx"),
//                Map.class, params);

        FileOutputStream fos = new FileOutputStream(OUTPUT_PATH + "ExcelVerifyTest.basetest.xlsx");
        result.getFailWorkbook().write(fos);
        fos.close();
        for (int i = 0; i < result.getList().size(); i++) {
            System.out.println(ReflectionToStringBuilder.toString(result.getList().get(i)));
        }

//        List<User> list = ExcelImportUtil.importExcel(new File(OUTPUT_PATH + "User.xlsx"), User.class, params);

        System.out.println(result.getList().size());
    }

    @Test
    public void testImportUser() throws Exception {
        ImportParams params = new ImportParams();
        params.setTitleRows(0);
        params.setHeadRows(1);
        params.setNeedVerify(true);
        params.setVerifyHandler(new ExcelVerifyHandlerImpl());
        params.setDictHandler(new ExcelDictHandlerImpl());

        ExcelImportResult<UserExcelVO> result = ExcelImportUtil.importExcelMore(
                new File(OUTPUT_PATH + "User.xlsx"),
                UserExcelVO.class, params);

        FileOutputStream fos = new FileOutputStream(OUTPUT_PATH + "ExcelVerifyTest.user.xlsx");
        result.getFailWorkbook().write(fos);
        fos.close();
        for (int i = 0; i < result.getList().size(); i++) {
            System.out.println(ReflectionToStringBuilder.toString(result.getList().get(i)));
        }

        System.out.println(result.getList().size());
    }

    public static class ExcelVerifyHandlerImpl implements IExcelVerifyHandler<UserExcelVO> {

        @Override
        public ExcelVerifyHandlerResult verifyHandler(UserExcelVO obj) {
            StringBuilder builder = new StringBuilder();
            if (StringUtils.isEmpty(obj.getName())) {
                builder.append("??????????????????");
            } else if (obj.getName().length() > 10) {
                builder.append("????????????????????????10");
            }
            return new ExcelVerifyHandlerResult(false, builder.toString());
        }

    }

    @Data
    public static class OrgMore implements IExcelModel, IExcelDataModel {
        @Excel(name = "??????", width = 50)
        @NotEmpty(message = "??????????????????")
        @Length(max = 25, message = "????????????????????????25")
        protected String label;
        private String errorMsg;
        private Integer rowNum;
        /**
         * ??????
         */
        @ApiModelProperty(value = "??????")
        @Length(max = 255, message = "????????????????????????255")
        @TableField(value = "abbreviation", condition = LIKE)
        @Excel(name = "??????", width = 30)
        private String abbreviation;


        /**
         * ?????????
         */
        @ApiModelProperty(value = "?????????")
        @Length(max = 255, message = "???????????????????????????255")
        @TableField(value = "tree_path", condition = LIKE)
        private String treePath;
    }


}
