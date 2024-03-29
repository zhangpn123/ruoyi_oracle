package com.ruoyi.project.system.dept.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import com.ruoyi.project.system.dept.domain.Dept;

/**
 * 部门管理 数据层
 * 
 * @author ruoyi
 */
public interface DeptMapper
{
    /**
     * 查询部门人数
     * 
     * @param dept 部门信息
     * @return 结果
     */
    public int selectDeptCount(Dept dept);

    /**
     * 查询部门是否存在用户
     * 
     * @param deptId 部门ID
     * @return 结果
     */
    public int checkDeptExistUser(String deptId);

    /**
     * 查询部门管理数据
     * 
     * @param dept 部门信息
     * @return 部门信息集合
     */
    public List<Dept> selectDeptList(Dept dept);

    /**
     * 删除部门管理信息
     * 
     * @param deptId 部门ID
     * @return 结果
     */
    public int deleteDeptById(String deptId);

    /**
     * 新增部门信息
     * 
     * @param dept 部门信息
     * @return 结果
     */
    public int insertDept(Dept dept);

    /**
     * 修改部门信息
     * 
     * @param dept 部门信息
     * @return 结果
     */
    public int updateDept(Dept dept);

    /**
     * 修改子元素关系
     * 
     * @param depts 子元素
     * @return 结果
     */
    public int updateDeptChildren(@Param("depts") List<Dept> depts);

    /**
     * 根据部门ID查询信息
     * 
     * @param deptId 部门ID
     * @return 部门信息
     */
    public Dept selectDeptById(String deptId);

    /**
     * 校验部门名称是否唯一
     * 
     * @param deptName 部门名称
     * @param parentId 父部门ID
     * @return 结果
     */
    public Dept checkDeptNameUnique(@Param("deptName") String deptName, @Param("parentId") String parentId);

    /**
     * 根据角色ID查询部门
     *
     * @param roleId 角色ID
     * @return 部门列表
     */
    public List<String> selectRoleDeptTree(Long roleId);

    /**
     * 修改所在部门的父级部门状态
     * 
     * @param dept 部门
     */
    public void updateDeptStatus(Dept dept);

    /**
     * 根据ID查询所有子部门
     * 
     * @param deptId 部门ID
     * @return 部门列表
     */
    public List<Dept> selectChildrenDeptById(String deptId);

    /**
     * 根据ID查询所有子部门（正常状态）
     * 
     * @param deptId 部门ID
     * @return 子部门数
     */
    public int selectNormalChildrenDeptById(String deptId);

    /**
     * 获取下一个最大的部门ID
     * @param parentId
     * @return
     */
    String selectMaxId(@Param("parentId") String parentId);

    List<Dept> selectDeptIdList();
}
