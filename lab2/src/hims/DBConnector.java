package hims;

import java.sql.*;

public class DBConnector {
	private static DBConnector instance = null;
	private Connection connection;
	private Connection transactionConnection;
	private Statement statement;
	private Statement transactionStatement;

	private DBConnector() throws ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
	}

	static public DBConnector getInstance() {
		try {
			if (instance == null)
				instance = new DBConnector();
		} catch (ClassNotFoundException e) {
			System.err.print("cannot load sql driver.");
			System.exit(1);
		}
		return instance;
	}

	public void connectDataBase(
			String hostName,
			Integer port,
			String dbName,
			String userName,
			String password) throws SQLException {
		String url = "jdbc:mysql://" + hostName +
				":" + port +
				"/" + dbName +
				"?zeroDateTimeBehavior=convertToNull&autoReconnect=true&characterEncoding=UTF-8&characterSetResults=UTF-8";
		connection = DriverManager.getConnection(url, userName, password);
		statement = connection.createStatement();
		transactionConnection = DriverManager.getConnection(url, userName, password);
		transactionConnection.setAutoCommit(false);
		transactionStatement = transactionConnection.createStatement();
	}


	public ResultSet getWholeTable(String tableName) {
		try {
			return statement.executeQuery("select * from " + tableName);
		} catch (SQLException e) {
			return null;
		}
	}

	public ResultSet getPatientInfo(String number) {
		try {
			return statement.executeQuery(
					"select * from " + Config.NameTablePatient +
							" where " + Config.NameTableColumnPatientNumber + "=" + number);
		} catch (SQLException e) {
			return null;
		}
	}

	public ResultSet getDoctorInfo(String number) {
		try {
			return statement.executeQuery(
					"select * from " + Config.NameTableDoctor +
							" where " + Config.NameTableColumnDoctorNumber + "=" + number);
		} catch (SQLException e) {
			return null;
		}
	}

	public int tryRegister(
			String registerCategoryNumber,
			String doctorNumber,
			String patientNumber,
			Double registerFee,
			boolean deductFromBalance,
			Double addToBalance) throws RegisterException {
		try {
			ResultSet result = transactionStatement.executeQuery(
					"select * from " + Config.NameTableRegister +
							" order by " + Config.NameTableColumnRegisterNumber +
							" desc limit 1"
			);
			int regNumber, currentCount;
			if (!result.next())
				regNumber = 0;
			else
				regNumber = Integer.parseInt(result.getString(Config.NameTableColumnRegisterNumber)) + 1;

			result = transactionStatement.executeQuery(
					"select * from " + Config.NameTableRegister +
							" where " + Config.NameTableColumnRegisterCategoryNumber +
							"=" + registerCategoryNumber +
							" order by " + Config.NameTableColumnCategoryRegisterNumber +
							" desc limit 1"
			);
			if (!result.next())
				currentCount = 0;
			else
				currentCount = result.getInt(Config.NameTableColumnRegisterCurrentRegisterCount);

			result = transactionStatement.executeQuery(
					"select * from " + Config.NameTablePatient +
							" where " + Config.NameTableColumnPatientNumber +
							"=" + patientNumber
			);
			if (!result.next())
				throw new RegisterException("patient does not exist", RegisterException.ErrorCode.patientNotExist);

			double balance = result.getDouble(Config.NameTableColumnPatientBalance);

			result = transactionStatement.executeQuery(
					"select " + Config.NameTableColumnCategoryRegisterMaxRegisterNumber +
							" from " + Config.NameTableCategoryRegister +
							" where " + Config.NameTableColumnCategoryRegisterNumber +
							"=" + registerCategoryNumber
			);
			int maxRegCount;
			if (!result.next())
				throw new RegisterException("illegal table entry",
						RegisterException.ErrorCode.registerCategoryNotFound);
			maxRegCount = result.getInt(Config.NameTableColumnCategoryRegisterMaxRegisterNumber);

			if (currentCount > maxRegCount) {
				throw new RegisterException("max register number reached",
						RegisterException.ErrorCode.registerNumberExceeded);
			}

			transactionStatement.executeUpdate(
					String.format(
							"insert into %s values (\"%06d\",\"%s\",\"%s\",\"%s\",%d,false,%s, current_timestamp)",
							Config.NameTableRegister,
							regNumber,
							registerCategoryNumber,
							doctorNumber,
							patientNumber,
							currentCount + 1,
							registerFee
					)
			);

			if (deductFromBalance) {
				transactionStatement.executeUpdate(
						String.format("update %s set %s=%.2f where %s=%s",
								Config.NameTablePatient,
								Config.NameTableColumnPatientBalance,
								(balance -= registerFee),
								Config.NameTableColumnPatientNumber,
								patientNumber)
				);
			}

			if (addToBalance != 0) {
				transactionStatement.executeUpdate(
						String.format("update %s set %s=%.2f where %s=%s",
								Config.NameTablePatient,
								Config.NameTableColumnPatientBalance,
								(balance += addToBalance),
								Config.NameTableColumnPatientNumber,
								patientNumber)
				);
			}

			transactionConnection.commit();
			return regNumber;
		} catch (SQLException e) {
			try {
				transactionConnection.rollback();
			} catch (SQLException ee) {
			}
			throw new RegisterException("sql exception occurred", RegisterException.ErrorCode.sqlException);
		}
	}

	public ResultSet getRegisterForDoctor(String docNumber, String startTime, String endTime) {
		try {
			String sql = "select reg." + Config.NameTableColumnRegisterNumber +
					",pat." + Config.NameTableColumnPatientName +
					",reg." + Config.NameTableColumnRegisterDateTime +
					",cat." + Config.NameTableColumnCategoryRegisterIsSpecialist + (
					" from (select " + Config.NameTableColumnRegisterNumber +
							"," + Config.NameTableColumnRegisterPatientNumber +
							"," + Config.NameTableColumnRegisterDateTime +
							"," + Config.NameTableColumnRegisterCategoryNumber +
							" from " + Config.NameTableRegister +
							" where " + Config.NameTableColumnRegisterDoctorNumber +
							"=" + docNumber +
							" and " + Config.NameTableColumnRegisterDateTime +
							">=\"" + startTime +
							"\" and " + Config.NameTableColumnRegisterDateTime +
							"<=\"" + endTime +
							"\") as reg") + (
					" inner join (select " + Config.NameTableColumnPatientNumber +
							"," + Config.NameTableColumnPatientName +
							" from " + Config.NameTablePatient +
							") as pat") +
					" on reg." + Config.NameTableColumnRegisterPatientNumber +
					"=pat." + Config.NameTableColumnPatientNumber + (
					" inner join (select " + Config.NameTableColumnCategoryRegisterNumber +
							"," + Config.NameTableColumnCategoryRegisterIsSpecialist +
							" from " + Config.NameTableCategoryRegister +
							") as cat") +
					" on reg." + Config.NameTableColumnRegisterCategoryNumber +
					"=cat." + Config.NameTableColumnCategoryRegisterNumber;
			return statement.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public ResultSet getIncomeInfo(String startTime, String endTime) {
		try {
			String sql = "select dep." + Config.NameTableColumnDepartmentName +
					" as depname,reg." + Config.NameTableColumnRegisterDoctorNumber +
					",doc." + Config.NameTableColumnDoctorName +
					" as docname,cat." + Config.NameTableColumnCategoryRegisterIsSpecialist +
					",reg." + Config.NameTableColumnRegisterCurrentRegisterCount +
					",SUM(reg." + Config.NameTableColumnRegisterFee +
					") as sum from" + (
					" (select * from " + Config.NameTableRegister +
							" where " + Config.NameTableColumnRegisterDateTime +
							">=\"" + startTime +
							"\" and " + Config.NameTableColumnRegisterDateTime +
							"<=\"" + endTime +
							"\") as reg") +
					" inner join" + (
					" (select " + Config.NameTableColumnDoctorNumber +
							"," + Config.NameTableColumnDoctorName +
							"," + Config.NameTableColumnDoctorDepartmentNumber +
							" from " + Config.NameTableDoctor +
							") as doc") +
					" on reg." + Config.NameTableColumnRegisterDoctorNumber +
					"=doc." + Config.NameTableColumnDoctorNumber +
					" inner join" + (
					" (select " + Config.NameTableColumnDepartmentNumber +
							"," + Config.NameTableColumnDepartmentName +
							" from " + Config.NameTableDepartment +
							") as dep") +
					" on doc." + Config.NameTableColumnDoctorDepartmentNumber +
					"=dep." + Config.NameTableColumnDepartmentNumber +
					" inner join" + (
					" (select " + Config.NameTableColumnCategoryRegisterNumber +
							"," + Config.NameTableColumnCategoryRegisterIsSpecialist +
							" from " + Config.NameTableCategoryRegister +
							") as cat") +
					" on reg." + Config.NameTableColumnRegisterCategoryNumber +
					"=cat." + Config.NameTableColumnCategoryRegisterNumber +
					" group by reg." + Config.NameTableColumnRegisterDoctorNumber +
					",cat." + Config.NameTableColumnCategoryRegisterIsSpecialist;
			return statement.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void updatePatientLoginTime(String patientId, String time) {
		try {
			statement.executeUpdate(
					"update " + Config.NameTablePatient +
							" set " + Config.NameTableColumnPatientLastLogin +
							"=\"" + time +
							"\" where " + Config.NameTableColumnPatientNumber +
							"=" + patientId
			);
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
	}

	public void updateDoctorLoginTime(String doctorId, String time) {
		try {
			statement.executeUpdate(
					"update " + Config.NameTableDoctor +
							" set " + Config.NameTableColumnDoctorLastLogin +
							"=\"" + time +
							"\" where " + Config.NameTableColumnRegisterDoctorNumber +
							"=" + doctorId
			);
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
	}
}

class RegisterException extends Exception {
	public enum ErrorCode {
		noError,
		registerCategoryNotFound,
		registerNumberExceeded,
		patientNotExist,
		sqlException,
		retryTimeExceeded,
	}

	ErrorCode error;

	RegisterException(String reason, ErrorCode err) {
		super(reason);
		error = err;
	}
}
