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
-- Table structure for table `xfpt_comment_reply`
--

DROP TABLE IF EXISTS `xfpt_comment_reply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `xfpt_comment_reply` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `comment_id` bigint NOT NULL COMMENT '所属评论ID',
  `order_id` bigint DEFAULT NULL COMMENT '订单ID',
  `order_item_id` bigint DEFAULT NULL COMMENT '订单明细ID',
  `goods_id` bigint DEFAULT NULL COMMENT '商品ID',
  `supplier_id` bigint DEFAULT NULL COMMENT '供应商ID',
  `from_user_id` bigint NOT NULL COMMENT '回复人ID',
  `from_user_name` varchar(100) NOT NULL COMMENT '回复人姓名快照',
  `from_role_type` char(1) NOT NULL COMMENT '回复人角色，1家长 2供应商',
  `from_role_title` varchar(20) NOT NULL COMMENT '回复人角色标题快照',
  `to_user_id` bigint DEFAULT NULL COMMENT '被回复人ID',
  `to_user_name` varchar(100) DEFAULT NULL COMMENT '被回复人姓名快照',
  `to_role_type` char(1) DEFAULT NULL COMMENT '被回复人角色，1家长 2供应商',
  `to_role_title` varchar(20) DEFAULT NULL COMMENT '被回复人角色标题快照',
  `content` text COMMENT '回复内容',
  `status` char(1) NOT NULL COMMENT '状态，0正常 1隐藏',
  `sort_num` int unsigned DEFAULT NULL COMMENT '排序值',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记，0正常 1删除',
  PRIMARY KEY (`id`),
  KEY `idx_xfpt_comment_reply_comment` (`comment_id`,`create_time`),
  KEY `idx_xfpt_comment_reply_order_item` (`order_item_id`,`create_time`),
  KEY `idx_xfpt_comment_reply_from_user` (`from_user_id`),
  KEY `idx_xfpt_comment_reply_supplier` (`supplier_id`),
  KEY `idx_xfpt_comment_reply_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=910001815 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品评论回复';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xfpt_comment_reply`
--

INSERT INTO `xfpt_comment_reply` VALUES (910001811,910001801,910001101,910001202,910000002,223,22301,'测试一客服','2','供应商',2184,'csyh','1','家长','感谢反馈，后续我们会继续优化面料和发货时效。','0',10,'seed','2026-05-01 17:40:00','seed','2026-05-01 17:40:00','SEED_SUPPLIER_CLOSED_LOOP_20260501','0'),(910001812,910001801,910001101,910001202,910000002,223,2184,'csyh','1','家长',22301,'测试一客服','2','供应商','收到，孩子穿着挺合身的，后面有新款还会继续看。','0',20,'seed','2026-05-01 17:48:00','seed','2026-05-01 17:48:00','SEED_SUPPLIER_CLOSED_LOOP_20260501','0'),(910001813,910001801,910001101,910001202,910000002,223,22301,'测试一客服','2','供应商',2184,'csyh','1','家长','好的，后续上新和活动会第一时间同步。','0',30,'seed','2026-05-01 17:55:00','seed','2026-05-01 17:55:00','SEED_SUPPLIER_CLOSED_LOOP_20260501','0'),(910001814,910001801,910001101,910001202,910000002,223,2184,'csyh','1','家长',22301,'测试一客服','2','供应商','谢谢，期待后续款式，也希望尺码说明继续保持清晰。','0',40,'seed','2026-05-01 18:03:00','seed','2026-05-01 18:03:00','SEED_SUPPLIER_CLOSED_LOOP_20260501','0');
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-03 19:52:46
