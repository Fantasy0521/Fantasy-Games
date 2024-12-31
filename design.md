```
随机排序，不分先后。欢迎交换友链~(￣▽￣)~*

* 昵称：Fantasy
* 一句话：游龙当归海，海不迎我自来也。
* 网址：[https://github.com/Fantasy0521](https://github.com/Fantasy0521)
* 头像URL：[http://localhost:8080/img/avatar.jpg](http://localhost:8080/img/avatar.jpg)

仅凭个人喜好添加友链，请在收到我的回复邮件后再于贵站添加本站链接。原则上已添加的友链不会删除，如果你发现自己被移除了，恕不另行通知，只需和我一样做就好。


```

![image-20241231110642578](design.assets/image-20241231110642578.png)

```
CREATE TABLE `game` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '游戏名称',
  `developers` varchar(255) NOT NULL COMMENT '开发商',
  `publisher` varchar(255) NOT NULL COMMENT '发行商',
  `publish_date` datetime NOT NULL COMMENT '发行日期',
  `series` varchar(255) NOT NULL COMMENT '系列',
  `official_url` varchar(255) NOT NULL COMMENT '官方地址',
  `download_url` varchar(255) NOT NULL COMMENT '下载地址',
  `first_picture` varchar(255) NOT NULL COMMENT '游戏首图，用于展示',
  `description` longtext NOT NULL COMMENT '描述',
  `is_published` bit(1) NOT NULL COMMENT '公开或私密',
  `is_recommend` bit(1) NOT NULL COMMENT '推荐开关',
  `is_appreciation` bit(1) NOT NULL COMMENT '赞赏开关',
  `is_comment_enabled` bit(1) NOT NULL COMMENT '评论开关',
  `views` int NOT NULL COMMENT '浏览次数',
  `stars` int NOT NULL COMMENT '收藏次数',
  `category_id` bigint NOT NULL COMMENT '游戏分类',
  `is_top` bit(1) NOT NULL COMMENT '是否置顶',
  `password` varchar(255) DEFAULT NULL COMMENT '提取码',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `index_game_category_id` (`category_id`) USING BTREE,
  KEY `index_game_developers` (`developers`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `game_image` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '游戏名称',
  `url` varchar(255) NOT NULL COMMENT '图片地址',
	`rn` int not null comment '顺序',
	`game_id` bigint NOT NULL COMMENT '游戏id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `index_game_image_game_id` (`game_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

```

