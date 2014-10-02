GRANT USAGE ON *.* TO 'slumber'@'localhost';
DROP USER 'slumber'@'localhost';
CREATE USER 'slumber'@'localhost' IDENTIFIED BY 'slumber1234';

drop database  if exists slumberdb;
create database slumberdb;
use slumberdb;

GRANT ALL ON slumberdb.* TO 'slumber'@'%';

use slumberdb;
