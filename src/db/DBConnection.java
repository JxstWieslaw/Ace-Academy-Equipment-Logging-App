package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBConnection {

    private static DBConnection dbConnection;//
    private Connection connection;// creating connection object

    private DBConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");// method used to call class containing external configuration file to connect to database. Afterwards
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/aceacademy?createDatabaseIfNotExist=true&allowMultiQueries=true", "root","");
            //linking to database online via xampp server.
            PreparedStatement pstm = connection.prepareStatement("SHOW TABLES");// Interface used to run many Sql queries.Dynamically.Right now it will show all non-temporary tables
            ResultSet resultSet = pstm.executeQuery();//The ResultSet interface provides getter methods for retrieving column values from the current row.
            if (!resultSet.next()) {
                String sql = "\n" +
                        "CREATE TABLE `equipmentdetail` (\n" +
                        "  `id` varchar(10) NOT NULL,\n" +
                        "  `description` varchar(15) DEFAULT NULL,\n" +
                        "  `branding` varchar(20) DEFAULT NULL,\n" +
                        "  `status` varchar(20) DEFAULT NULL,\n" +
                        "  PRIMARY KEY (`id`)\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n" +
                        "\n" +
                        "CREATE TABLE `equipmentborrowed` (\n" +
                        "  `borrowedId` varchar(10) NOT NULL,\n" +
                        "  `date` date DEFAULT NULL,\n" +
                        "  `memberId` varchar(10) DEFAULT NULL,\n" +
                        "  `equipmentid` varchar(10) DEFAULT NULL,\n" +
                        "  PRIMARY KEY (`borrowedId`)\n" +
                        "  CONSTRAINT FOREIGN KEY (`memberId`) REFERENCES `membershipdetail` (`id`),\n" +
                        "  CONSTRAINT FOREIGN KEY (`equipmentid`) REFERENCES `equipmentdetail` (`id`)\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n" +//InnoDB is a database storage engine. database storage engine is by which tables are stored, retrieved and handled.
                        "\n" +
                        "CREATE TABLE `membershipdetail` (\n" +
                        "  `id` varchar(11) NOT NULL,\n" +
                        "  `name` varchar(50) DEFAULT NULL,\n" +
                        "  `address` varchar(50) DEFAULT NULL,\n" +
                        "  `contact` varchar(12) DEFAULT NULL,\n" +
                        "  PRIMARY KEY (`id`),\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n" +
                        "\n" +
                        "CREATE TABLE `equipmentreturn` (\n" +
                        "  `id` int(11) NOT NULL,\n" +
                        "  `borrowedDate` date NOT NULL,\n" +
                        "  `returnedDate` date DEFAULT NULL,\n" +
                        "  `fine` int(10) DEFAULT NULL,\n" +
                        "  `borrowedId` int(10) DEFAULT NULL,\n" +
                        "  PRIMARY KEY (`id`),\n" +
                        "  CONSTRAINT FOREIGN KEY (`borrowedId`) REFERENCES `equipmentborrowed` (`borrowedId`),\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n";
                pstm = connection.prepareStatement(sql);
                pstm.execute();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);//Callers of that method aren't required to catch the exception, or acknowledge it in any way.Its an unchecked exception
        }

    }

    public static DBConnection getInstance() {
        return dbConnection = ((dbConnection == null) ? new DBConnection() : dbConnection);

    }

    public Connection getConnection() {
        return connection;
    }


}
