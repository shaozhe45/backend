package com.changqing.gov.authority.controller.auth;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import cn.afterturn.easypoi.view.PoiBaseView;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.authority.api.UserBizApi;
import com.changqing.gov.authority.controller.poi.ExcelUserVerifyHandlerImpl;
import com.changqing.gov.authority.dto.auth.UserExcelVO;
import com.changqing.gov.authority.dto.auth.UserPageDTO;
import com.changqing.gov.authority.dto.auth.UserRoleDTO;
import com.changqing.gov.authority.dto.auth.UserSaveDTO;
import com.changqing.gov.authority.dto.auth.UserUpdateAvatarDTO;
import com.changqing.gov.authority.dto.auth.UserUpdateBaseInfoDTO;
import com.changqing.gov.authority.dto.auth.UserUpdateDTO;
import com.changqing.gov.authority.dto.auth.UserUpdatePasswordDTO;
import com.changqing.gov.authority.entity.auth.User;
import com.changqing.gov.authority.entity.core.Org;
import com.changqing.gov.authority.service.auth.UserService;
import com.changqing.gov.authority.service.common.DictionaryItemService;
import com.changqing.gov.authority.service.core.OrgService;
import com.changqing.gov.base.R;
import com.changqing.gov.base.controller.SuperCacheController;
import com.changqing.gov.base.entity.Entity;
import com.changqing.gov.base.entity.SuperEntity;
import com.changqing.gov.base.request.PageParams;
import com.changqing.gov.common.constant.BizConstant;
import com.changqing.gov.common.constant.DictionaryType;
import com.changqing.gov.database.mybatis.conditions.query.LbqWrapper;
import com.changqing.gov.database.mybatis.conditions.query.QueryWrap;
import com.changqing.gov.injection.core.InjectionCore;
import com.changqing.gov.log.annotation.SysLog;
import com.changqing.gov.model.RemoteData;
import com.changqing.gov.security.annotation.PreAuth;
import com.changqing.gov.utils.MapHelper;
import com.changqing.gov.utils.StrPool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.groups.Default;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 用户
 * </p>
 *
 * @author changqing
 * @date 2019-07-22
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/user")
@Api(value = "User", tags = "用户")
//@PreAuth(replace = "user:")
public class UserController extends SuperCacheController<UserService, Long, User, UserPageDTO, UserSaveDTO, UserUpdateDTO> implements UserBizApi {
    @Autowired
    private OrgService orgService;
    @Autowired
    private ExcelUserVerifyHandlerImpl excelUserVerifyHandler;
    @Autowired
    private DictionaryItemService dictionaryItemService;
    @Autowired
    private InjectionCore injectionCore;
    @Autowired
    private UserService userService;

    /**
     * 重写保存逻辑
     *
     * @param data
     * @return
     */
    @Override
    public R<User> handlerSave(UserSaveDTO data) {
        User user = BeanUtil.toBean(data, User.class);
        if (user.getAccount() != null) {
            User userInfo = userService.selectByAccount(user.getAccount());
            if (userInfo == null || "".equals(userInfo)) {
                user.setReadonly(false);
                baseService.saveUser(user);
                return success(user);
            }else {
                return R.fail(444, "已存在相同账户的用户，不可重复添加！");
            }
        } else {
            return R.fail("添加失败，请重新添加！");
        }

    }

    @Override
    public void handlerQueryParams(PageParams<UserPageDTO> params){
        params.getModel().setIs_delete(false);
    }

    @Override
    public R<IPage<User>> page(@RequestBody @Validated PageParams<UserPageDTO> params) {
        this.handlerQueryParams(params);
        IPage page = params.buildPage();
        this.query(params, page, (Long)null);
        return this.success(page);
    }
    /**
     * 重写删除逻辑
     *
     * @param ids
     * @return
     */
    @Override
    public R<Boolean> handlerDelete(List<Long> ids) {
        //        baseService.remove(ids);
        for(Long id : ids){
            User user = new User();
            user.setId(id);
            user.setIsDelete(true);
            baseService.updateById(user);
        }
        return success(true);
    }

    /**
     * 重写修改逻辑
     *
     * @param data
     * @return
     */
    @Override
    public R<User> handlerUpdate(UserUpdateDTO data) {
        User user = BeanUtil.toBean(data, User.class);
        baseService.updateUser(user);
        return success(user);
    }

    /**
     * 修改
     *
     * @param data
     * @return
     */
    @ApiOperation(value = "修改基础信息")
    @PutMapping("/base")
    @SysLog(value = "'修改基础信息:' + #data?.id", request = false)
    @PreAuth("hasPermit('{}update')")
    public R<User> updateBase(@RequestBody @Validated({SuperEntity.Update.class}) UserUpdateBaseInfoDTO data) {
        User user = BeanUtil.toBean(data, User.class);
        baseService.updateById(user);
        return success(user);
    }

    /**
     * 修改头像
     *
     * @param data
     * @return
     */
    @ApiOperation(value = "修改头像", notes = "修改头像")
    @PutMapping("/avatar")
    @SysLog("'修改头像:' + #p0.id")
    public R<User> avatar(@RequestBody @Validated(SuperEntity.Update.class) UserUpdateAvatarDTO data) {
        User user = BeanUtil.toBean(data, User.class);
        baseService.updateById(user);
        return success(user);
    }

    /**
     * 修改密码
     *
     * @param data 修改实体
     * @return
     */
    @ApiOperation(value = "修改密码", notes = "修改密码")
    @PutMapping("/password")
    @SysLog("'修改密码:' + #p0.id")
    public R<Boolean> updatePassword(@RequestBody @Validated(SuperEntity.Update.class) UserUpdatePasswordDTO data) {
        return success(baseService.updatePassword(data));
    }

    /**
     * 重置密码
     *
     * @param data
     * @return
     */
    @ApiOperation(value = "重置密码", notes = "重置密码")
    @PostMapping("/reset")
    @SysLog("'重置密码:' + #data.id")
    public R<Boolean> reset(@RequestBody @Validated(SuperEntity.Update.class) UserUpdatePasswordDTO data) {
        baseService.reset(data);
        return success();
    }

    /**
     * 查询角色的已关联用户
     *
     * @param roleId  角色id
     * @param keyword 账号或名称
     * @return
     */
    @ApiOperation(value = "查询角色的已关联用户", notes = "查询角色的已关联用户")
    @GetMapping(value = "/role/{roleId}")
    public R<UserRoleDTO> findUserByRoleId(@PathVariable("roleId") Long roleId, @RequestParam(value = "keyword", required = false) String keyword) {
        List<User> list = baseService.findUserByRoleId(roleId, keyword);
        List<Long> idList = list.stream().mapToLong(User::getId).boxed().collect(Collectors.toList());
        return success(UserRoleDTO.builder().idList(idList).userList(list).build());
    }


    @ApiOperation(value = "查询所有用户", notes = "查询所有用户")
    @GetMapping("/find")
    @SysLog("查询所有用户")
    @Override
    public R<List<Long>> findAllUserId() {
        return success(baseService.findAllUserId());
    }

    @ApiOperation(value = "查询所有用户实体", notes = "查询所有用户实体")
    @GetMapping("/findAll")
    @SysLog("查询所有用户")
    public R<List<User>> findAll() {
        List<User> res = baseService.list();
        res.forEach(obj -> obj.setPassword(null));
        return success(res);
    }

    @Override
    @RequestMapping(value = "/findUserById", method = RequestMethod.GET)
    public R<List<User>> findUserById(@RequestParam(value = "ids") List<Long> ids) {
        return this.success(baseService.findUserById(ids));
    }

    @Override
    public R<Boolean> importExcel(@RequestParam("file") MultipartFile simpleFile, HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
        ImportParams params = new ImportParams();
        params.setTitleRows(0);
        params.setHeadRows(1);
        params.setNeedVerify(true);
        params.setVerifyGroup(new Class[]{Default.class});
        params.setVerifyHandler(excelUserVerifyHandler);
        //用官方提供的DictHandler有性能问题
//        params.setDictHandler();
        ExcelImportResult<UserExcelVO> result = ExcelImportUtil.importExcelMore(simpleFile.getInputStream(), UserExcelVO.class, params);

        if (result.isVerifyFail()) {
            String errorMsgs = result.getFailList().stream().map((item) -> StrUtil.format("第{}行检验错误: {}", item.getRowNum(), item.getErrorMsg()))
                    .collect(Collectors.joining("<br/>"));
            return R.validFail(errorMsgs);
        }

        List<UserExcelVO> list = result.getList();
        if (list.isEmpty()) {
            return this.validFail("导入数据不能为空");
        }
        // 获取组织机构，岗位列表；以名称为key，id为value
        Map<String,Long> stationMap=userService.getCoreStation();
        Map<String,Long> orgMap=userService.getCoreOrg();

        //数据转换
        Map<String, Map<String, String>> dictMap = dictionaryItemService.map(new String[]{DictionaryType.EDUCATION, DictionaryType.NATION, DictionaryType.POSITION_STATUS});

        Map<String, String> educationMap = MapHelper.inverse(dictMap.get(DictionaryType.EDUCATION));
        Map<String, String> nationMap = MapHelper.inverse(dictMap.get(DictionaryType.NATION));
        Map<String, String> positionStatusMap = MapHelper.inverse(dictMap.get(DictionaryType.POSITION_STATUS));

        List<User> userList = list.stream().map((item) -> {
            User user = new User();
            String[] ignore = new String[]{
                    "org", "station", "nation", "education", "positionStatus"
            };
            BeanUtil.copyProperties(item, user, ignore);
            user.setOrg(new RemoteData<>(orgMap.getOrDefault(item.getOrg(),null)));
            user.setStation(new RemoteData<>(stationMap.getOrDefault(item.getStation(),null)));
            user.setEducation(new RemoteData<>(educationMap.getOrDefault(item.getEducation(), StrPool.EMPTY)));
            user.setNation(new RemoteData<>(nationMap.getOrDefault(item.getNation(), StrPool.EMPTY)));
            user.setPositionStatus(new RemoteData<>(positionStatusMap.getOrDefault(item.getPositionStatus(), StrPool.EMPTY)));
            user.setPassword(BizConstant.DEF_PASSWORD_MD5);
            return user;
        }).collect(Collectors.toList());

        baseService.saveBatch(userList);

        return this.success(true);
    }


    /**
     * 分页、导出、导出预览 方法的共用查询条件
     *
     * @param params
     * @param page
     * @param defSize
     */
    @Override
    public void query(PageParams<UserPageDTO> params, IPage<User> page, Long defSize) {
        UserPageDTO userPage = params.getModel();

        QueryWrap<User> wrap = handlerWrapper(null, params);

        LbqWrapper<User> wrapper = wrap.lambda();
        if (userPage.getOrg() != null && RemoteData.getKey(userPage.getOrg(), 0L) > 0) {
            List<Org> children = orgService.findChildren(Arrays.asList(userPage.getOrg().getKey()));
            wrapper.in(User::getOrg, children.stream().map((org) -> new RemoteData(org.getId())).collect(Collectors.toList()));
        }
        wrapper.like(User::getName, userPage.getName())
                .like(User::getAccount, userPage.getAccount())
                .eq(User::getReadonly, false)
                .like(User::getEmail, userPage.getEmail())
                .like(User::getMobile, userPage.getMobile())
                .eq(User::getStation, userPage.getStation())
                .eq(User::getPositionStatus, userPage.getPositionStatus())
                .eq(User::getEducation, userPage.getEducation())
                .eq(userPage.getNation() != null && StrUtil.isNotEmpty(userPage.getNation().getKey()), User::getNation, userPage.getNation())
                .eq(User::getSex, userPage.getSex())
                .eq(User::getStatus, userPage.getStatus())
                .eq(User::getIsDelete,userPage.getIs_delete());
        baseService.findPage(page, wrapper);
        // 手动注入
        injectionCore.injection(page);
    }
    @Override
    public void exportExcel(PageParams<UserPageDTO> params, HttpServletRequest request, HttpServletResponse response) {
        IPage<User> page = params.buildPage();
        params.getModel().setIs_delete(false);
        ExportParams exportParams = this.getExportParams(params, page);

        List<User> userRecordsList=page.getRecords();
        for (User user :userRecordsList){
            if (user.getOrg()!=null&&user.getOrg().getData()!=null) {
                user.setOrgName(user.getOrg().getData().getLabel());
            }
        }
        Map<String, Object> map = new HashMap(5);
        map.put("data", userRecordsList);
        map.put("entity", this.getEntityClass());
        map.put("params", exportParams);
        String fileName = (String)params.getMap().getOrDefault("fileName", "临时文件");
        map.put("fileName", fileName);
        PoiBaseView.render(map, request, response, "easypoiExcelView");
    }
}
