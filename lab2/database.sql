-- MySQL dump 10.16  Distrib 10.1.32-MariaDB, for Linux (x86_64)
--
-- Host: localhost    Database: java_lab2
-- ------------------------------------------------------
-- Server version	10.1.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `department`
--

DROP TABLE IF EXISTS `department`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `department` (
  `depid` char(6) NOT NULL,
  `name` char(10) NOT NULL,
  `py` char(8) NOT NULL,
  PRIMARY KEY (`depid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `department`
--

LOCK TABLES `department` WRITE;
/*!40000 ALTER TABLE `department` DISABLE KEYS */;
INSERT INTO `department` VALUES ('01','内科','neike'),('02','外科','waike'),('03','眼科','yanke'),('04','儿科','erke'),('05','精神科','jingshenke'),('06','中医科','zhongyike');
/*!40000 ALTER TABLE `department` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doctor`
--

DROP TABLE IF EXISTS `doctor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `doctor` (
  `docid` char(6) NOT NULL,
  `depid` char(6) NOT NULL,
  `name` char(10) NOT NULL,
  `py` char(4) DEFAULT NULL,
  `password` char(8) NOT NULL,
  `speciallist` tinyint(1) NOT NULL,
  `last_login_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`docid`),
  KEY `depid` (`depid`),
  CONSTRAINT `doctor_ibfk_1` FOREIGN KEY (`depid`) REFERENCES `department` (`depid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctor`
--

LOCK TABLES `doctor` WRITE;
/*!40000 ALTER TABLE `doctor` DISABLE KEYS */;
INSERT INTO `doctor` VALUES ('001','01','刘医生','liu','123456',0,'2018-05-03 16:52:15'),('002','01','张医生','zhang','123456',1,'0000-00-00 00:00:00'),('003','02','周医生','zhou','123456',0,'0000-00-00 00:00:00'),('004','02','胡医生','hu','123456',1,'0000-00-00 00:00:00'),('005','03','方医生','fang','123456',0,'0000-00-00 00:00:00'),('006','03','孙医生','sun','123456',1,'0000-00-00 00:00:00'),('007','04','杜医生','du','123456',0,'0000-00-00 00:00:00'),('008','04','李医生','li','123456',1,'0000-00-00 00:00:00'),('009','05','付医生','fu','123456',0,'0000-00-00 00:00:00'),('010','05','谭医生','tan','123456',1,'0000-00-00 00:00:00'),('011','06','乔医生','qiao','123456',0,'0000-00-00 00:00:00'),('012','06','丁医生','ding','123456',1,'0000-00-00 00:00:00');
/*!40000 ALTER TABLE `doctor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patient`
--

DROP TABLE IF EXISTS `patient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `patient` (
  `pid` char(6) NOT NULL,
  `name` char(10) NOT NULL,
  `password` char(8) NOT NULL,
  `balance` decimal(10,2) NOT NULL,
  `last_login_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patient`
--

LOCK TABLES `patient` WRITE;
/*!40000 ALTER TABLE `patient` DISABLE KEYS */;
INSERT INTO `patient` VALUES ('001','王志浩','654321',1000.00,'2018-05-03 17:43:33'),('002','包靖远','654321',1000.00,'0000-00-00 00:00:00'),('003','刘洋','654321',1000.00,'0000-00-00 00:00:00'),('004','罗有为','654321',1000.00,'0000-00-00 00:00:00'),('005','胡晋源','654321',1000.00,'0000-00-00 00:00:00'),('006','李佳强','654321',1000.00,'0000-00-00 00:00:00');
/*!40000 ALTER TABLE `patient` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `register`
--

DROP TABLE IF EXISTS `register`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `register` (
  `reg_id` char(6) NOT NULL,
  `catid` char(6) NOT NULL,
  `docid` char(6) NOT NULL,
  `pid` char(6) NOT NULL,
  `current_reg_count` int(11) NOT NULL,
  `unreg` tinyint(1) NOT NULL,
  `reg_fee` decimal(8,2) NOT NULL,
  `reg_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`reg_id`),
  KEY `catid` (`catid`),
  KEY `docid` (`docid`),
  KEY `pid` (`pid`),
  CONSTRAINT `register_ibfk_1` FOREIGN KEY (`catid`) REFERENCES `register_category` (`catid`),
  CONSTRAINT `register_ibfk_2` FOREIGN KEY (`docid`) REFERENCES `doctor` (`docid`),
  CONSTRAINT `register_ibfk_3` FOREIGN KEY (`pid`) REFERENCES `patient` (`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `register_category`
--

DROP TABLE IF EXISTS `register_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `register_category` (
  `catid` char(6) NOT NULL,
  `name` char(12) NOT NULL,
  `py` char(4) NOT NULL,
  `depid` char(6) NOT NULL,
  `speciallist` tinyint(1) NOT NULL,
  `max_reg_number` int(11) NOT NULL,
  `reg_fee` decimal(10,2) NOT NULL,
  PRIMARY KEY (`catid`),
  KEY `depid` (`depid`),
  CONSTRAINT `register_category_ibfk_1` FOREIGN KEY (`depid`) REFERENCES `department` (`depid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `register_category`
--

LOCK TABLES `register_category` WRITE;
/*!40000 ALTER TABLE `register_category` DISABLE KEYS */;
INSERT INTO `register_category` VALUES ('001','心血管内科','xinxueguan','01',1,23,24.70),('002','心血管内科','xinxueguan','01',0,23,6.43),('003','神经内科','shenjing','01',1,23,21.29),('004','神经内科','shenjing','01',0,23,9.21),('005','神经外科','shenjing','02',1,17,58.53),('006','神经外科','shenjing','02',0,17,14.20),('007','胸外科','xiong','02',1,16,47.43),('008','胸外科','xiong','02',0,16,10.85),('009','小儿眼科','xiaoer','03',1,13,42.22),('010','眼外伤','yanwaishang','03',0,13,5.19),('011','眼外伤','yanwaishang','03',1,13,25.29),('012','角膜科','jiaomo','03',0,13,14.91),('013','小儿康复科','xiaoerkangfu','04',1,15,57.61),('014','小儿康复科','xiaoerkangfu','04',0,15,6.85),('015','小儿皮肤科','xiaoerpifu','04',1,29,31.46),('016','小儿皮肤科','xiaoerpifu','04',0,29,7.63),('017','心理咨询','xinlizixun','05',1,16,59.69),('018','心理咨询','xinlizixun','05',0,16,14.49),('019','精神障碍科','jingshenzhangai','05',1,24,29.19),('020','精神障碍科','jingshenzhangai','05',0,24,8.93),('021','中医内科','zhongyineike','06',1,20,48.18),('022','中医内科','zhongyineike','06',0,20,7.32),('023','中医骨伤科','zhongyigushang','06',1,13,29.77),('024','中医骨伤科','zhongyigushang','06',0,13,14.56);
/*!40000 ALTER TABLE `register_category` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-05-04  1:45:21