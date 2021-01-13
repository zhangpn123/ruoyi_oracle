package com.ruoyi.project.system.dept.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import com.ruoyi.framework.web.domain.ZtreeStr;
import com.ruoyi.project.system.user.domain.User;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.security.ShiroUtils;
import com.ruoyi.framework.aspectj.lang.annotation.DataScope;
import com.ruoyi.framework.web.domain.Ztree;
import com.ruoyi.project.system.dept.domain.Dept;
import com.ruoyi.project.system.dept.mapper.DeptMapper;
import com.ruoyi.project.system.role.domain.Role;

/**
 * 部门管理 服务实现
 *
 * @author ruoyi
 */
@Service
public class DeptServiceImpl implements IDeptService {
    @Autowired
    private DeptMapper deptMapper;

    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    @Override
    // @DataScope(deptAlias = "d")
    public List<Dept> selectDeptList(Dept dept) {
        // 获取当前的用户
        User currentUser = ShiroUtils.getSysUser();
        if (StringUtils.isEmpty(dept.getDeptId())) {
            dept.setDeptId(currentUser.getDeptId());
        } else {
            if (dept.getDeptId().length() < currentUser.getDeptId().length()) {
                return new ArrayList<Dept>();
            } else {
                if (!dept.getDeptId().startsWith(currentUser.getDeptId())) {
                    return new ArrayList<Dept>();
                }
            }
        }
        return deptMapper.selectDeptList(dept);
    }

    /**
     * 查询部门管理树
     *
     * @param dept 部门信息
     * @return 所有部门信息
     */
    @Override
    // @DataScope(deptAlias = "d")
    public List<ZtreeStr> selectDeptTree(Dept dept) {
        // 获取当前的用户
        User currentUser = ShiroUtils.getSysUser();
        dept.setDeptId(currentUser.getDeptId());
        List<Dept> deptList = deptMapper.selectDeptList(dept);
        List<ZtreeStr> ztrees = initZtree(deptList);
        return ztrees;
    }

    /**
     * 查询部门管理树（排除下级）
     *
     * @param deptId 部门ID
     * @return 所有部门信息
     */
    @Override
    @DataScope(deptAlias = "d")
    public List<ZtreeStr> selectDeptTreeExcludeChild(Dept dept) {
        String deptId = dept.getDeptId();
        List<Dept> deptList = deptMapper.selectDeptList(dept);
        Iterator<Dept> it = deptList.iterator();
        while (it.hasNext()) {
            Dept d = (Dept) it.next();
            if (d.getDeptId() == deptId || ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), deptId + "")) {
                it.remove();
            }
        }
        List<ZtreeStr> ztrees = initZtree(deptList);
        return ztrees;
    }

    /**
     * 根据角色ID查询部门（数据权限）
     *
     * @param role 角色对象
     * @return 部门列表（数据权限）
     */
    @Override
    public List<ZtreeStr> roleDeptTreeData(Role role) {
        Long roleId = role.getRoleId();
        List<ZtreeStr> ztrees = new ArrayList<ZtreeStr>();
        List<Dept> deptList = selectDeptList(new Dept());
        if (StringUtils.isNotNull(roleId)) {
            List<String> roleDeptList = deptMapper.selectRoleDeptTree(roleId);
            ztrees = initZtree(deptList, roleDeptList);
        } else {
            ztrees = initZtree(deptList);
        }
        return ztrees;
    }

    /**
     * 对象转部门树
     *
     * @param deptList 部门列表
     * @return 树结构列表
     */
    public List<ZtreeStr> initZtree(List<Dept> deptList) {
        return initZtree(deptList, null);
    }

    /**
     * 对象转部门树
     *
     * @param deptList     部门列表
     * @param roleDeptList 角色已存在菜单列表
     * @return 树结构列表
     */
    public List<ZtreeStr> initZtree(List<Dept> deptList, List<String> roleDeptList) {
        List<ZtreeStr> ztrees = new ArrayList<ZtreeStr>();
        boolean isCheck = StringUtils.isNotNull(roleDeptList);
        for (Dept dept : deptList) {
            if (UserConstants.DEPT_NORMAL.equals(dept.getStatus())) {
                ZtreeStr ztree = new ZtreeStr();
                ztree.setId(dept.getDeptId());
                ztree.setpId(dept.getParentId());
                ztree.setName(dept.getDeptName());
                ztree.setTitle(dept.getDeptName());
                if (isCheck) {
                    ztree.setChecked(roleDeptList.contains(dept.getDeptId() + dept.getDeptName()));
                }
                ztrees.add(ztree);
            }
        }
        return ztrees;
    }

    /**
     * 查询部门人数
     *
     * @param parentId 部门ID
     * @return 结果
     */
    @Override
    public int selectDeptCount(String parentId) {
        Dept dept = new Dept();
        dept.setParentId(parentId);
        return deptMapper.selectDeptCount(dept);
    }

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    @Override
    public boolean checkDeptExistUser(String deptId) {
        int result = deptMapper.checkDeptExistUser(deptId);
        return result > 0 ? true : false;
    }

    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public int deleteDeptById(String deptId) {
        return deptMapper.deleteDeptById(deptId);
    }

    /**
     * 新增保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public int insertDept(Dept dept) {
        Dept info = deptMapper.selectDeptById(dept.getParentId());
        // 如果父节点不为"正常"状态,则不允许新增子节点
        if (!UserConstants.DEPT_NORMAL.equals(info.getStatus())) {
            throw new BusinessException("部门停用，不允许新增");
        }
        /*获取部门ID*/
        String parentId = info.getDeptId();
        String maxId = deptMapper.selectMaxId(parentId);
        String temp = "0";
        if (!StringUtils.isEmpty(maxId)) {
            temp = maxId.substring(maxId.length() - 3, maxId.length());
        }
        int next = Integer.parseInt(temp) + 1;
        String deptId = parentId + StringUtils.addZeroLeft(String.valueOf(next), 3);

        dept.setDeptId(deptId);
        dept.setCreateBy(ShiroUtils.getLoginName());
        dept.setAncestors(info.getAncestors() + "," + dept.getParentId());
        return deptMapper.insertDept(dept);
    }


    /**
     * 修改保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateDept(Dept dept) {
        Dept newParentDept = deptMapper.selectDeptById(dept.getParentId());
        Dept oldDept = selectDeptById(dept.getDeptId());
        if (StringUtils.isNotNull(newParentDept) && StringUtils.isNotNull(oldDept)) {
            String newAncestors = newParentDept.getAncestors() + "," + newParentDept.getDeptId();
            String oldAncestors = oldDept.getAncestors();
            dept.setAncestors(newAncestors);
            updateDeptChildren(dept.getDeptId(), newAncestors, oldAncestors);
        }
        dept.setUpdateBy(ShiroUtils.getLoginName());
        int result = deptMapper.updateDept(dept);
        if (UserConstants.DEPT_NORMAL.equals(dept.getStatus())) {
            // 如果该部门是启用状态，则启用该部门的所有上级部门
            updateParentDeptStatus(dept);
        }
        return result;
    }

    /**
     * 修改该部门的父级部门状态
     *
     * @param dept 当前部门
     */
    private void updateParentDeptStatus(Dept dept) {
        String updateBy = dept.getUpdateBy();
        dept = deptMapper.selectDeptById(dept.getDeptId());
        dept.setUpdateBy(updateBy);
        deptMapper.updateDeptStatus(dept);
    }

    /**
     * 修改子元素关系
     *
     * @param deptId       被修改的部门ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    public void updateDeptChildren(String deptId, String newAncestors, String oldAncestors) {
        List<Dept> children = deptMapper.selectChildrenDeptById(deptId);
        for (Dept child : children) {
            child.setAncestors(child.getAncestors().replace(oldAncestors, newAncestors));
        }
        if (children.size() > 0) {
            deptMapper.updateDeptChildren(children);
        }
    }

    /**
     * 修改子元素关系
     *
     * @param deptId    部门ID
     * @param ancestors 元素列表
     */
    public void updateDeptChildren(String deptId, String ancestors) {
        Dept dept = new Dept();
        dept.setParentId(deptId);
        List<Dept> childrens = deptMapper.selectDeptList(dept);
        for (Dept children : childrens) {
            children.setAncestors(ancestors + "," + dept.getParentId());
        }
        if (childrens.size() > 0) {
            deptMapper.updateDeptChildren(childrens);
        }
    }

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    @Override
    public Dept selectDeptById(String deptId) {
        return deptMapper.selectDeptById(deptId);
    }

    /**
     * 根据ID查询所有子部门（正常状态）
     *
     * @param deptId 部门ID
     * @return 子部门数
     */
    @Override
    public int selectNormalChildrenDeptById(String deptId) {
        return deptMapper.selectNormalChildrenDeptById(deptId);
    }

    /**
     * 校验部门名称是否唯一
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public String checkDeptNameUnique(Dept dept) {
        String deptId = StringUtils.isNull(dept.getDeptId()) ? null : dept.getDeptId();
        Dept info = deptMapper.checkDeptNameUnique(dept.getDeptName(), dept.getParentId());
        if (StringUtils.isNotNull(info) && info.getDeptId() != deptId) {
            return UserConstants.DEPT_NAME_NOT_UNIQUE;
        }
        return UserConstants.DEPT_NAME_UNIQUE;
    }

    @Override
    public List<Dept> selectDeptIdList() {
        return deptMapper.selectDeptIdList();
    }
}
