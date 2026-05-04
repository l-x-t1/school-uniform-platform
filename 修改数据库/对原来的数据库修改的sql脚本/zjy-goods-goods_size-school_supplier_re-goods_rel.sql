-- MySQL dump 10.13  Distrib 8.0.12, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: xfglpt
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8mb4 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `xfpt_goods_size`
--

DROP TABLE IF EXISTS `xfpt_goods_size`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `xfpt_goods_size` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `goods_id` bigint NOT NULL COMMENT '关联商品',
  `size_code` varchar(32) NOT NULL COMMENT '尺码编码',
  `sort_num` int unsigned NOT NULL COMMENT '排序字段 根据商品尺码依次增加 S 1 M 2 L 3 XL 4. ....',
  `gender_type` char(1) DEFAULT NULL COMMENT '性别类型，0通用 1男 2女',
  `measure_json` longtext COMMENT '量体规则JSON文本',
  `retail_price` decimal(18,2) NOT NULL COMMENT '零售价',
  `group_price` decimal(18,2) DEFAULT NULL COMMENT '统购价',
  `stock_num` int unsigned NOT NULL COMMENT '库存',
  `status` char(1) NOT NULL COMMENT '状态，0停用 1启用',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记，0正常 1删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_xfpt_goods_size_code` (`goods_id`,`size_code`),
  KEY `idx_xfpt_goods_size_goods` (`goods_id`),
  KEY `idx_xfpt_goods_size_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=910000203 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品尺码';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xfpt_goods_size`
--

LOCK TABLES `xfpt_goods_size` WRITE;
/*!40000 ALTER TABLE `xfpt_goods_size` DISABLE KEYS */;
INSERT INTO `xfpt_goods_size` VALUES (910000101,910000001,'S',1,'0','{\"heightRange\":\"135-150\",\"weightRange\":\"30-42\"}',139.00,120.00,200,'1','seed','2026-05-01 09:10:00',NULL,NULL,'SEED_SUPPLIER_CLOSED_LOOP_20260501','0'),(910000102,910000001,'M',2,'0','{\"heightRange\":\"150-165\",\"weightRange\":\"42-55\"}',139.00,120.00,200,'1','seed','2026-05-01 09:10:00',NULL,NULL,'SEED_SUPPLIER_CLOSED_LOOP_20260501','0'),(910000103,910000001,'L',3,'0','{\"heightRange\":\"165-175\",\"weightRange\":\"55-70\"}',139.00,120.00,200,'1','seed','2026-05-01 09:10:00',NULL,NULL,'SEED_SUPPLIER_CLOSED_LOOP_20260501','0'),(910000201,910000002,'M',2,'0','{\"heightRange\":\"150-165\",\"weightRange\":\"42-55\"}',199.00,180.00,160,'1','seed','2026-05-01 09:12:00',NULL,NULL,'SEED_SUPPLIER_CLOSED_LOOP_20260501','0'),(910000202,910000002,'L',3,'0','{\"heightRange\":\"165-175\",\"weightRange\":\"55-70\"}',199.00,180.00,160,'1','seed','2026-05-01 09:12:00',NULL,NULL,'SEED_SUPPLIER_CLOSED_LOOP_20260501','0');
/*!40000 ALTER TABLE `xfpt_goods_size` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xfpt_goods`
--

DROP TABLE IF EXISTS `xfpt_goods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `xfpt_goods` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `goods_name` varchar(200) NOT NULL COMMENT '商品名称',
  `goods_code` varchar(64) NOT NULL COMMENT '商品编码',
  `category_name` varchar(100) NOT NULL COMMENT '品类名称',
  `supplier_id` bigint NOT NULL COMMENT '供应商ID',
  `supplier_name` varchar(200) NOT NULL COMMENT '供应商名称快照',
  `brand_name` varchar(100) DEFAULT NULL COMMENT '品牌名称',
  `main_image` varchar(500) DEFAULT NULL COMMENT '主图路径或URL',
  `detail_images_json` longtext COMMENT '详情图JSON文本',
  `detail_content` longtext COMMENT '商品详情富文本',
  `sale_type` char(1) NOT NULL COMMENT '销售类型，1统购 2零售 3统购零售',
  `status` char(1) NOT NULL COMMENT '状态，0草稿 1启用 2停用',
  `publish_status` char(1) NOT NULL COMMENT '上架状态，0未上架 1已上架 2已下架',
  `publish_time` datetime DEFAULT NULL COMMENT '上架时间',
  `off_shelf_time` datetime DEFAULT NULL COMMENT '下架时间',
  `sort_num` int unsigned DEFAULT NULL COMMENT '排序值',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记，0正常 1删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_xfpt_goods_code` (`goods_code`),
  KEY `idx_xfpt_goods_supplier` (`supplier_id`),
  KEY `idx_xfpt_goods_status` (`status`,`publish_status`)
) ENGINE=InnoDB AUTO_INCREMENT=910000005 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xfpt_goods`
--

LOCK TABLES `xfpt_goods` WRITE;
/*!40000 ALTER TABLE `xfpt_goods` DISABLE KEYS */;
INSERT INTO `xfpt_goods` VALUES (910000001,'闭环测试-夏季短袖校服套装1','SEED-GOODS-SUMMER-001','夏季校服',223,'测试一','麒麟优选','/seed/images/summer_uniform.jpg','[\"/seed/images/summer_uniform_1.jpg\",\"/seed/images/summer_uniform_2.jpg\"]','闭环测试数据：夏季短袖上衣和短裤套装。','3','1','1','2026-05-01 00:00:00',NULL,10,'seed','2026-05-01 09:00:00','张三','2026-05-02 16:47:23','SEED_SUPPLIER_CLOSED_LOOP_20260501','0'),(910000002,'闭环测试-春秋运动校服套装','SEED-GOODS-SPRING-001','春秋校服',223,'测试一','麒麟优选','/seed/images/spring_uniform.jpg','[\"/seed/images/spring_uniform_1.jpg\",\"/seed/images/spring_uniform_2.jpg\"]','<p>闭环测试数据：春秋运动外套和长裤套装。</p>','3','1','1','2026-05-01 09:05:00',NULL,20,'seed','2026-05-01 09:05:00',NULL,NULL,'SEED_SUPPLIER_CLOSED_LOOP_20260501','0'),(910000003,'test2','test2','春季测试',124,'test2','test2',NULL,'test2','test2','2','2','2','2026-05-02 00:00:00','2026-05-27 00:00:00',NULL,'张三','2026-05-02 17:42:25','张三','2026-05-02 18:01:42','test1','1'),(910000004,'test3','test3','test3',123,'test3','test3',NULL,'test3','test3','3','1','1','2026-05-02 00:00:00','2026-05-08 00:00:00',NULL,'张三','2026-05-02 18:03:20','张三','2026-05-02 18:04:03','test3','1');
/*!40000 ALTER TABLE `xfpt_goods` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xfpt_school_supplier_rel`
--

DROP TABLE IF EXISTS `xfpt_school_supplier_rel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `xfpt_school_supplier_rel` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `school_id` bigint NOT NULL COMMENT '学校ID',
  `school_name` varchar(200) NOT NULL COMMENT '学校名称快照',
  `supplier_id` bigint NOT NULL COMMENT '供应商ID',
  `supplier_name` varchar(200) NOT NULL COMMENT '供应商名称快照',
  `notice_id` bigint DEFAULT NULL COMMENT '统购公告ID',
  `contract_id` bigint DEFAULT NULL COMMENT '合同ID',
  `cooperate_status` char(1) NOT NULL COMMENT '合作状态，1合作中 2已取消 3历史合作 4公告期内',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记，0正常 1删除',
  PRIMARY KEY (`id`),
  KEY `idx_xfpt_school_supplier_rel_status` (`school_id`,`supplier_id`,`cooperate_status`),
  KEY `idx_xfpt_school_supplier_rel_notice` (`notice_id`)
) ENGINE=InnoDB AUTO_INCREMENT=910000702 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学校供应商合作关系';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xfpt_school_supplier_rel`
--

LOCK TABLES `xfpt_school_supplier_rel` WRITE;
/*!40000 ALTER TABLE `xfpt_school_supplier_rel` DISABLE KEYS */;
INSERT INTO `xfpt_school_supplier_rel` VALUES (910000701,442,'石家庄市麒麟小学',223,'测试一',910000501,910030001,'1','seed','2026-05-01 11:00:00',NULL,NULL,'SEED_SUPPLIER_CLOSED_LOOP_20260501','0');
/*!40000 ALTER TABLE `xfpt_school_supplier_rel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xfpt_school_supplier_goods_rel`
--

DROP TABLE IF EXISTS `xfpt_school_supplier_goods_rel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `xfpt_school_supplier_goods_rel` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `rel_id` bigint NOT NULL COMMENT '合作关系主表ID',
  `school_id` bigint NOT NULL COMMENT '学校ID',
  `supplier_id` bigint NOT NULL COMMENT '供应商ID',
  `goods_id` bigint NOT NULL COMMENT '商品ID',
  `status` char(1) NOT NULL COMMENT '状态，0停用 1启用',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记，0正常 1删除',
  PRIMARY KEY (`id`),
  KEY `idx_xfpt_school_supplier_goods_rel_rel` (`rel_id`),
  KEY `idx_xfpt_school_supplier_goods_rel_goods` (`goods_id`)
) ENGINE=InnoDB AUTO_INCREMENT=910000713 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学校供应商合作商品';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xfpt_school_supplier_goods_rel`
--

LOCK TABLES `xfpt_school_supplier_goods_rel` WRITE;
/*!40000 ALTER TABLE `xfpt_school_supplier_goods_rel` DISABLE KEYS */;
INSERT INTO `xfpt_school_supplier_goods_rel` VALUES (910000711,910000701,442,223,910000001,'1','seed','2026-05-01 11:05:00',NULL,NULL,'SEED_SUPPLIER_CLOSED_LOOP_20260501','0'),(910000712,910000701,442,223,910000002,'1','seed','2026-05-01 11:06:00',NULL,NULL,'SEED_SUPPLIER_CLOSED_LOOP_20260501','0');
/*!40000 ALTER TABLE `xfpt_school_supplier_goods_rel` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-03 17:39:19
