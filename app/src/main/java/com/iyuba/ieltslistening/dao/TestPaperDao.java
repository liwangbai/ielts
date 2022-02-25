package com.iyuba.ieltslistening.dao;

import com.iyuba.ieltslistening.pojo.Sections;
import com.iyuba.ieltslistening.pojo.TestPaper;

import java.util.List;

/**
 * 查询试卷列表的接口
 */
public interface TestPaperDao {

    /**
     * 查询id对应的试题(列表数据,题目内容在另外的表里)是否本地存在，不存在则存入数据库
     * @param id  试卷对应id
     * @return 返回值不为null均表示存在
     */
    Integer findExistByTitleID(int id);

    /**
     * 试题(列表数据,题目内容在另外的表里)存入库
     * @param testPaper
     */
    void addTestPager(TestPaper testPaper);

    /**
     * 查询本地所有试卷(列表信息)
     * @return 试卷信息列表
     */
    List<TestPaper> findAllTestPaper();

    /**
     * 查询id对应的试题的Section列表数据是否本地存在，不存在则存入数据库
     * @param id 试卷对应id
     * @return 返回值不为null均表示存在
     */
    Integer findSectionExistByPaperId(int id);

    /**
     * 添加试题对应的Section
     * @param sections 需要存入的Section对象
     * @param paperId 该Section属于哪一个paperId对应的试题
     */
    void addSection(Sections sections, int paperId);

    /**
     * 根据试卷的id查询对应的Sections
     * @param paperId 试卷Id
     * @return 试卷对用的Sections列表
     */
    List<Sections> findSectionsByPaperId(int paperId);
}
