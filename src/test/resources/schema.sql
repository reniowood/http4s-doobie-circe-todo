CREATE TABLE IF NOT EXISTS `todo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` text NOT NULL,
  `is_done` tinyint(4) NOT NULL DEFAULT '0',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `pre_todo` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `todo_id` INT NOT NULL,
  `pre_todo_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `unique_pre_todo` (`todo_id`, `pre_todo_id`)
);
