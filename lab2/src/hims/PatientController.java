package hims;

import com.jfoenix.controls.*;
import com.sun.javafx.robot.impl.FXRobotHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javafx.concurrent.*;

abstract class ListItem {
	public String pronounce;

	@Override
	public abstract String toString();

	public abstract void fromSqlResult(ResultSet result) throws SQLException;

	public String getPronounce() {
		return pronounce;
	}
}

class ListItemNameDepartment extends ListItem {
	public String number;
	public String name;

	@Override
	public String toString() {
		return number + " " + name + " ";
	}

	@Override
	public void fromSqlResult(ResultSet result) throws SQLException {
		number = result.getString(Config.NameTableColumnDepartmentNumber);
		name = result.getString(Config.NameTableColumnDepartmentName);
		pronounce = result.getString(Config.NameTableColumnDepartmentPronounce);
	}
}

class ListItemNameDoctor extends ListItem {
	public String number;
	public String departmentNumber;
	public String name;
	public boolean isSpecialist;
	public Timestamp lastLogin;
	public String password;

	@Override
	public String toString() {
		return number + " " + name + " " + (isSpecialist ? "专家" : "普通医师");
	}

	@Override
	public void fromSqlResult(ResultSet result) throws SQLException {
		number = result.getString(Config.NameTableColumnDoctorNumber);
		departmentNumber = result.getString(Config.NameTableColumnDoctorDepartmentNumber);
		name = result.getString(Config.NameTableColumnDoctorName);
		isSpecialist = result.getBoolean(Config.NameTableColumnDoctorIsSpecialist);
		lastLogin = result.getTimestamp(Config.NameTableColumnDoctorLastLogin);
		password = result.getString(Config.NameTableColumnDoctorPassword);
		pronounce = result.getString(Config.NameTableColumnDoctorPronounce);
	}
}

class ListItemTypeRegister extends ListItem {
	public boolean isSpecialist;

	@Override
	public String toString() {
		return isSpecialist ? "专家号" : "普通号";
	}

	@Override
	public void fromSqlResult(ResultSet result) {

	}
}

class ListItemNameRegister extends ListItem {
	public String number;
	public String name;
	public Float fee;
	public String department;
	public boolean isSpecialist;
	public int maxNumber;

	@Override
	public String toString() {
		return number + " " + name + " " + (isSpecialist ? "专家号" : "普通号") + "¥ " + fee;
	}

	@Override
	public void fromSqlResult(ResultSet result) throws SQLException {
		number = result.getString(Config.NameTableColumnCategoryRegisterNumber);
		name = result.getString(Config.NameTableColumnCategoryRegisterName);
		pronounce = result.getString(Config.NameTableColumnCategoryRegisterPronounce);
		department = result.getString(Config.NameTableColumnCategoryRegisterDepartment);
		isSpecialist = result.getBoolean(Config.NameTableColumnCategoryRegisterIsSpecialist);
		maxNumber = result.getInt(Config.NameTableColumnCategoryRegisterMaxRegisterNumber);
		fee = result.getFloat(Config.NameTableColumnCategoryRegisterFee);
	}
}

public class PatientController {
	static public String patientName;
	static public String patientNumber;
	static public Double patientBalance;

	@FXML
	private GridPane mainPane;

	@FXML
	private Label labelWelcome;
	@FXML
	private JFXComboBox<String> inputNameDepartment;
	@FXML
	private JFXComboBox<String> inputNameDoctor;
	@FXML
	private JFXComboBox<String> inputTypeRegister;
	@FXML
	private JFXComboBox<String> inputNameRegister;
	@FXML
	private Label labelFee;
	@FXML
	private Label labelRefund;
	@FXML
	private Label labelStatus;
	@FXML
	private JFXTextField inputMoney;
	@FXML
	private JFXButton buttonRegister;
	@FXML
	private JFXButton buttonExit;
	@FXML
	private JFXCheckBox checkBoxUseBalance;
	@FXML
	private JFXCheckBox checkBoxAddToBalance;

	private int lastIndexInputNameDepartment = -1;
	private int lastIndexInputNameDoctor = -1;
	private int lastIndexInputTypeRegister = -1;
	private int lastIndexInputNameRegister = -1;

	private ObservableList<ListItemNameDepartment> listNameDepartment = FXCollections.observableArrayList();
	private ObservableList<ListItemNameDoctor> listNameDoctor = FXCollections.observableArrayList();
	private ObservableList<ListItemTypeRegister> listTypeRegister = FXCollections.observableArrayList();
	private ObservableList<ListItemNameRegister> listNameRegister = FXCollections.observableArrayList();

	private ObservableList<ListItemNameDepartment> listNameDepartmentFiltered = FXCollections.observableArrayList();
	private ObservableList<ListItemNameDoctor> listNameDoctorFiltered = FXCollections.observableArrayList();
	private ObservableList<ListItemTypeRegister> listTypeRegisterFiltered = FXCollections.observableArrayList();
	private ObservableList<ListItemNameRegister> listNameRegisterFiltered = FXCollections.observableArrayList();

	private <ItemType extends ListItem> boolean updateOneSetOfData(
			String tableName,
			ObservableList<ItemType> list,
			Class<ItemType> clazz) {

		ResultSet result = DBConnector.getInstance().getWholeTable(tableName);

		if (result != null) {
			ObservableList<ItemType> tempList = FXCollections.observableArrayList();
			try {
				while (result.next()) {
					ItemType item = clazz.newInstance();
					item.fromSqlResult(result);
					tempList.add(item);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
			list.clear();
			list.addAll(tempList);
			return true;
		}
		return true;
	}

	private int getMoneyData() {
		if (inputMoney.getText().isEmpty())
			return 0;
		else
			return Integer.parseInt(inputMoney.getText().trim());
	}

	public void updateData() {
		updateOneSetOfData(
				Config.NameTableDepartment,
				listNameDepartment,
				ListItemNameDepartment.class
		);

		updateOneSetOfData(
				Config.NameTableDoctor,
				listNameDoctor,
				ListItemNameDoctor.class
		);

		updateOneSetOfData(
				Config.NameTableCategoryRegister,
				listNameRegister,
				ListItemNameRegister.class
		);
		ListItemTypeRegister itemSpecialist = new ListItemTypeRegister();
		ListItemTypeRegister itemNormal = new ListItemTypeRegister();
		itemSpecialist.isSpecialist = true;
		itemSpecialist.pronounce = "zhuanjiahao";
		itemNormal.isSpecialist = false;
		itemNormal.pronounce = "potonghao";
		listTypeRegister.clear();
		listTypeRegister.add(itemSpecialist);
		listTypeRegister.add(itemNormal);
	}


	private void updateRefund() {
		int index = inputNameRegister.getSelectionModel().getSelectedIndex();
		if (index != -1 && checkBoxUseBalance.isSelected()) {
			labelRefund.setText("¥ 0");
			labelRefund.setStyle("");
			return;
		}

		if (index != -1 && getMoneyData() > listNameRegisterFiltered.get(index).fee) {
			labelRefund.setText(String.format("¥ %.2f", getMoneyData() - listNameRegisterFiltered.get(index).fee));
			labelRefund.setStyle("");
		} else if (index != -1) {
			labelRefund.setText("交款金额不足");
			labelRefund.setStyle("-fx-text-fill: red;");
		}
	}

	private void updateUseBalance() {
		int index = inputNameRegister.getSelectionModel().getSelectedIndex();
		if (index != -1 && patientBalance < listNameRegisterFiltered.get(index).fee) {
			checkBoxUseBalance.setSelected(false);
			inputMoney.setDisable(false);
			checkBoxUseBalance.setText("余额不足");
			checkBoxUseBalance.setDisable(true);
		} else {
			checkBoxUseBalance.setDisable(false);
			checkBoxUseBalance.setText("使用余额付款");
			checkBoxUseBalance.setSelected(true);
			inputMoney.setDisable(true);
		}
	}

	private void updateRegisterButton() {
		buttonRegister.setDisable(true);
		int index;
		if (inputNameDoctor.getSelectionModel().getSelectedIndex() != -1 &&
				(index = inputNameRegister.getSelectionModel().getSelectedIndex()) != -1 &&
				((checkBoxUseBalance.isSelected() && patientBalance >= listNameRegisterFiltered.get(index).fee) ||
						(!checkBoxUseBalance.isSelected() && getMoneyData() >= listNameRegisterFiltered.get(index).fee))) {
			buttonRegister.setDisable(false);
		}
	}

	@FXML
	void useBalanceClicked() {
		if (checkBoxUseBalance.isSelected()) {
			inputMoney.setDisable(true);
			updateRefund();
		} else {
			inputMoney.setDisable(false);
			updateRefund();
		}
		updateRegisterButton();
	}

	private void updateUserDisplayInfo() {
		labelWelcome.setText(String.format("欢迎进入医院挂号系统，%s！    余额：¥ %.2f", patientName, patientBalance));
	}

	@FXML
	public void initialize() {
		updateUserDisplayInfo();
		updateData();
		inputNameDepartment.setItems(FXCollections.observableArrayList());
		inputNameDoctor.setItems(FXCollections.observableArrayList());
		inputTypeRegister.setItems(FXCollections.observableArrayList());
		inputNameRegister.setItems(FXCollections.observableArrayList());
		reFilterDepartment(false);
		reFilterDoctor(false);
		reFilterRegisterType(false);
		reFilterRegisterName(false);
		updateRegisterButton();
		inputNameDepartment.getEditor().setOnKeyReleased(keyEvent -> {
			if (shouldSupressKeyCode(keyEvent.getCode()))
				return;
			reFilterDepartment(true);
			reFilterDoctor(false);
			reFilterRegisterType(false);
			reFilterRegisterName(false);
			if (!inputNameDepartment.isShowing()) {
				inputNameDepartment.show();
			} else {
				inputNameDepartment.hide();
				inputNameDepartment.show();
			}
		});
		inputNameDepartment.addEventHandler(ComboBox.ON_HIDDEN, e -> {
			int index;
			if ((index = inputNameDepartment.getSelectionModel().getSelectedIndex())
					!= lastIndexInputNameDepartment) {
				lastIndexInputNameDepartment = index;
				reFilterDoctor(false);
				reFilterRegisterType(false);
				reFilterRegisterName(false);
			}
			e.consume();
		});


		inputNameDoctor.getEditor().setOnKeyReleased(keyEvent -> {
			if (shouldSupressKeyCode(keyEvent.getCode()))
				return;
			reFilterDoctor(true);
			reFilterDepartment(false);
			reFilterRegisterType(false);
			reFilterRegisterName(false);
			if (!inputNameDoctor.isShowing()) {
				inputNameDoctor.show();
			} else {
				inputNameDoctor.hide();
				inputNameDoctor.show();
			}
		});
		inputNameDoctor.addEventHandler(ComboBox.ON_HIDDEN, e -> {
			int index;
			if ((index = inputNameDoctor.getSelectionModel().getSelectedIndex())
					!= lastIndexInputNameDoctor) {
				lastIndexInputNameDoctor = index;
				reFilterDepartment(false);
				reFilterRegisterType(false);
				reFilterRegisterName(false);
			}
			inputNameDoctor.setStyle("");
			updateRegisterButton();
			e.consume();
		});
		inputNameDoctor.setOnMousePressed(mouseEvent -> {
			inputNameDoctor.setStyle("");
		});

		inputTypeRegister.getEditor().setOnKeyReleased(keyEvent -> {
			if (shouldSupressKeyCode(keyEvent.getCode()))
				return;
			reFilterRegisterType(true);
			reFilterDepartment(false);
			reFilterDoctor(false);
			reFilterRegisterName(false);
			if (!inputTypeRegister.isShowing()) {
				inputTypeRegister.show();
			} else {
				inputTypeRegister.hide();
				inputTypeRegister.show();
			}
		});
		inputTypeRegister.addEventHandler(ComboBox.ON_HIDDEN, e -> {
			int index;
			if ((index = inputTypeRegister.getSelectionModel().getSelectedIndex())
					!= lastIndexInputTypeRegister) {
				lastIndexInputTypeRegister = index;
				reFilterDepartment(false);
				reFilterDoctor(false);
				reFilterRegisterName(false);
			}
			updateRegisterButton();
			e.consume();
		});

		inputNameRegister.getEditor().setOnKeyReleased(keyEvent -> {
			if (shouldSupressKeyCode(keyEvent.getCode()))
				return;
			reFilterRegisterName(true);
			reFilterDepartment(false);
			reFilterDoctor(false);
			reFilterRegisterType(false);
			if (!inputNameRegister.isShowing()) {
				inputNameRegister.show();
			} else {
				inputNameRegister.hide();
				inputNameRegister.show();
			}
		});
		inputNameRegister.addEventHandler(ComboBox.ON_HIDDEN, e -> {
			int index;
			if ((index = inputNameRegister.getSelectionModel().getSelectedIndex())
					!= lastIndexInputNameRegister) {
				lastIndexInputNameRegister = index;
				reFilterDepartment(false);
				reFilterDoctor(false);
				reFilterRegisterType(false);
			}
			inputNameRegister.setStyle("");
			if (index != -1) {
				float fee = listNameRegisterFiltered.get(index).fee;
				labelFee.setText("¥ " + fee);
			}
			updateUseBalance();
			updateRefund();
			updateRegisterButton();
			e.consume();
		});
		inputNameRegister.setOnMousePressed(mouseEvent -> {
			inputNameRegister.setStyle("");
		});
		buttonRegister.setOnKeyReleased(keyEvent -> {
			if (keyEvent.getCode() == KeyCode.ENTER)
				buttonRegisterPressed();
		});

		buttonExit.setOnKeyReleased(keyEvent -> {
			try {
				if (keyEvent.getCode() == KeyCode.ENTER)
					buttonExitClicked();
			} catch (IOException e) {
			}
		});

		checkBoxUseBalance.setOnKeyReleased(keyEvent -> {
			if (keyEvent.getCode() == KeyCode.SPACE)
				useBalanceClicked();
			else
				keyEvent.consume();
		});

	}

	@FXML
	private void InputMoneyFinished() {
		updateRefund();
		updateRegisterButton();
	}

	@FXML
	private void buttonRegisterPressed() {
		if (inputNameDoctor.getSelectionModel().getSelectedIndex() == -1) {
			statusError("请选择医生姓名");
			inputNameDoctor.setStyle("-fx-background-color: pink;");
			return;
		}
		if (inputNameRegister.getSelectionModel().getSelectedIndex() == -1) {
			statusError("请选择号种名称");
			inputNameRegister.setStyle("-fx-background-color: pink;");
			return;
		}
		int index;
		if (!((index = inputNameRegister.getSelectionModel().getSelectedIndex()) != -1 &&
				inputNameDoctor.getSelectionModel().getSelectedIndex() != -1 && (
				(checkBoxUseBalance.isSelected() && patientBalance >= listNameRegisterFiltered.get(index).fee) ||
						(!checkBoxUseBalance.isSelected() && getMoneyData() >= listNameRegisterFiltered.get(index).fee)))) {
			statusError("缴费金额不足或余额不足");
			return;
		}

		disableEverything();
		TryRegisterService service = new TryRegisterService(
				listNameRegisterFiltered.get(inputNameRegister.getSelectionModel().getSelectedIndex()).number,
				listNameDoctorFiltered.get(inputNameDoctor.getSelectionModel().getSelectedIndex()).number,
				patientNumber,
				listNameRegisterFiltered.get(inputNameRegister.getSelectionModel().getSelectedIndex()).fee,
				checkBoxUseBalance.isSelected(),
				((!checkBoxUseBalance.isSelected() && checkBoxAddToBalance.isSelected()) ?
						getMoneyData() - listNameRegisterFiltered.get(index).fee : 0)
		);
		service.setOnSucceeded(workerStateEvent -> {
			switch (service.returnCode) {
				case registerNumberExceeded:
					statusError("此号已达到人数上限。");
					break;
				case registerCategoryNotFound:
				case sqlException:
					statusError("数据库错误, 请联系管理员");
					break;
				case retryTimeExceeded:
					statusError("系统繁忙，请稍候再试");
					break;
				case noError:
					labelStatus.setText("挂号成功，挂号号码：" + service.registerNumber);
					patientBalance = service.updatedBalance;
					updateUserDisplayInfo();
					break;
			}
			enableEverything();
		});
		service.start();
	}

	private void disableEverything() {
		mainPane.setDisable(true);
	}

	private void enableEverything() {
		mainPane.setDisable(false);
	}

	private void statusError(String error) {
		labelStatus.setText(error);
		labelStatus.setStyle("-fx-text-fill: red;");
	}

	private void reFilterDepartment(boolean withoutSelect) {
		int index;
		String previousKey = "";
		if ((index = inputNameDepartment.getSelectionModel().getSelectedIndex()) != -1)
			previousKey = listNameDepartmentFiltered.get(index).number;

		ObservableList<ListItemNameDepartment> list0 = FXCollections.observableArrayList();
		ObservableList<ListItemNameDepartment> list1 = FXCollections.observableArrayList();

		for (ListItemNameDepartment listItemNameDepartment : listNameDepartment) {
			if (listItemNameDepartment.toString().contains(inputNameDepartment.getEditor().getText().trim()) ||
					listItemNameDepartment.getPronounce().contains(inputNameDepartment.getEditor().getText().trim())) {
				listNameDepartmentFiltered.add(listItemNameDepartment);
				list0.add(listItemNameDepartment);
			}
		}

		if ((index = inputNameDoctor.getSelectionModel().getSelectedIndex()) != -1) {
			for (ListItemNameDepartment department : list0)
				if (department.number.equals(listNameDoctorFiltered.get(index).departmentNumber))
					list1.add(department);
			list0 = list1;
		}

		boolean isCurrentInputLegal = false;
		int counter = 0, newSelection = -1;
		inputNameDepartment.getItems().clear();
		listNameDepartmentFiltered.clear();
		for (ListItemNameDepartment department : list0) {
			inputNameDepartment.getItems().add(department.toString());
			listNameDepartmentFiltered.add(department);
			if (department.toString().contains(inputNameDepartment.getEditor().getText().trim()) ||
					department.getPronounce().contains(inputNameDepartment.getEditor().getText().trim()))
				isCurrentInputLegal = true;
			if (previousKey.equals(department.number))
				newSelection = counter;
			++counter;
		}

		if (!withoutSelect) {
			if (!isCurrentInputLegal)
				inputNameDepartment.getEditor().clear();
			if (newSelection != -1) {
				inputNameDepartment.getSelectionModel().clearAndSelect(newSelection);
				inputNameDepartment.getEditor().setText(inputNameDepartment.getItems().get(newSelection));
			}
		}
	}

	private void reFilterDoctor(boolean withoutSelect) {
		int index;
		String previousKey = "";
		if ((index = inputNameDoctor.getSelectionModel().getSelectedIndex()) != -1)
			previousKey = listNameDoctorFiltered.get(index).number;

		ObservableList<ListItemNameDoctor> list0 = FXCollections.observableArrayList();
		ObservableList<ListItemNameDoctor> list1 = FXCollections.observableArrayList();

		for (ListItemNameDoctor listItemNameDoctor : listNameDoctor)
			if (listItemNameDoctor.toString().contains(inputNameDoctor.getEditor().getText().trim()) ||
					listItemNameDoctor.getPronounce().contains(inputNameDoctor.getEditor().getText().trim()))
				list0.add(listItemNameDoctor);

		if ((index = inputNameDepartment.getSelectionModel().getSelectedIndex()) != -1) {
			for (ListItemNameDoctor listItemNameDoctor : list0)
				if (listItemNameDoctor.departmentNumber.equals(listNameDepartmentFiltered.get(index).number))
					list1.add(listItemNameDoctor);
			list0 = list1;
		}

		list1 = FXCollections.observableArrayList();
		if ((index = inputTypeRegister.getSelectionModel().getSelectedIndex()) != -1) {
			for (ListItemNameDoctor doctor : list0)
				if (doctor.isSpecialist || !listTypeRegisterFiltered.get(index).isSpecialist)
					list1.add(doctor);
			list0 = list1;
		}

		list1 = FXCollections.observableArrayList();
		if ((index = inputNameRegister.getSelectionModel().getSelectedIndex()) != -1) {
			for (ListItemNameDoctor doctor : list0)
				if (doctor.departmentNumber.equals(listNameRegisterFiltered.get(index).department))
					list1.add(doctor);
			list0 = list1;
		}

		boolean isCurrentInputLegal = false;
		int counter = 0, newSelection = -1;
		inputNameDoctor.getItems().clear();
		listNameDoctorFiltered.clear();
		for (ListItemNameDoctor doctor : list0) {
			listNameDoctorFiltered.add(doctor);
			inputNameDoctor.getItems().add(doctor.toString());
			if (doctor.toString().contains(inputNameDoctor.getEditor().getText().trim()) ||
					doctor.getPronounce().contains(inputNameDoctor.getEditor().getText().trim()))
				isCurrentInputLegal = true;
			if (previousKey.equals(doctor.number))
				newSelection = counter;
			++counter;
		}

		if (!withoutSelect) {
			if (!isCurrentInputLegal)
				inputNameDoctor.getEditor().clear();
			if (newSelection != -1) {
				inputNameDoctor.getSelectionModel().clearAndSelect(counter);
				inputNameDoctor.getEditor().setText(inputNameDoctor.getItems().get(newSelection));
			}
		}
	}

	private void reFilterRegisterType(boolean withoutSelect) {
		int index;
		String previousKey = "";
		if ((index = inputTypeRegister.getSelectionModel().getSelectedIndex()) != -1)
			previousKey = listTypeRegisterFiltered.get(index).pronounce;

		ObservableList<ListItemTypeRegister> list0 = FXCollections.observableArrayList();
		ObservableList<ListItemTypeRegister> list1 = FXCollections.observableArrayList();

		for (ListItemTypeRegister listItemTypeRegister : listTypeRegister)
			if (listItemTypeRegister.toString().contains(inputTypeRegister.getEditor().getText().trim()) ||
					listItemTypeRegister.getPronounce().contains(inputTypeRegister.getEditor().getText().trim()))
				list0.add(listItemTypeRegister);

		if ((index = inputNameDoctor.getSelectionModel().getSelectedIndex()) != -1) {
			for (ListItemTypeRegister listItemTypeRegister : list0)
				if (listNameDoctorFiltered.get(index).isSpecialist || !listItemTypeRegister.isSpecialist)
					list1.add(listItemTypeRegister);
			list0 = list1;
		}

		list1 = FXCollections.observableArrayList();
		if ((index = inputNameRegister.getSelectionModel().getSelectedIndex()) != -1) {
			for (ListItemTypeRegister register : list0)
				if (register.isSpecialist == listNameRegisterFiltered.get(index).isSpecialist)
					list1.add(register);
			list0 = list1;
		}

		boolean isCurrentInputLegal = false;
		int counter = 0, newSelection = -1;
		listTypeRegisterFiltered.clear();
		inputTypeRegister.getItems().clear();
		for (ListItemTypeRegister register : list0) {
			listTypeRegisterFiltered.add(register);
			inputTypeRegister.getItems().add(register.toString());
			if (register.toString().contains(inputTypeRegister.getEditor().getText().trim()) ||
					register.getPronounce().contains(inputTypeRegister.getEditor().getText().trim()))
				isCurrentInputLegal = true;
			if (previousKey.equals(register.pronounce))
				newSelection = counter;
			++counter;
		}

		if (!withoutSelect) {
			if (!isCurrentInputLegal)
				inputTypeRegister.getEditor().clear();
			if (newSelection != -1) {
				inputTypeRegister.getSelectionModel().clearAndSelect(newSelection);
				inputTypeRegister.getEditor().setText(inputTypeRegister.getItems().get(newSelection));
			}
		}
	}

	private void reFilterRegisterName(boolean withoutSelect) {
		int index;
		String previousKey = "";
		if ((index = inputNameRegister.getSelectionModel().getSelectedIndex()) != -1)
			previousKey = listNameRegisterFiltered.get(index).number;

		ObservableList<ListItemNameRegister> list0 = FXCollections.observableArrayList();
		ObservableList<ListItemNameRegister> list1 = FXCollections.observableArrayList();

		for (ListItemNameRegister listItemNameRegister : listNameRegister)
			if (listItemNameRegister.toString().contains(inputNameRegister.getEditor().getText().trim()) ||
					listItemNameRegister.getPronounce().contains(inputNameRegister.getEditor().getText().trim()))
				list0.add(listItemNameRegister);

		if ((index = inputNameDepartment.getSelectionModel().getSelectedIndex()) != -1) {
			for (ListItemNameRegister listItemNameRegister : list0)
				if (listItemNameRegister.department.equals(listNameDepartmentFiltered.get(index).number))
					list1.add(listItemNameRegister);
			list0 = list1;
		}

		list1 = FXCollections.observableArrayList();
		if ((index = inputNameDoctor.getSelectionModel().getSelectedIndex()) != -1) {
			for (ListItemNameRegister listItemNameRegister : list0)
				if (!listItemNameRegister.isSpecialist || listNameDoctorFiltered.get(index).isSpecialist)
					list1.add(listItemNameRegister);
			list0 = list1;
		}

		list1 = FXCollections.observableArrayList();
		if ((index = inputTypeRegister.getSelectionModel().getSelectedIndex()) != -1) {
			for (ListItemNameRegister listItemNameRegister : list0)
				if (listItemNameRegister.isSpecialist == listTypeRegisterFiltered.get(index).isSpecialist)
					list1.add(listItemNameRegister);
			list0 = list1;
		}

		boolean isCurrentInputLegal = false;
		int counter = 0, newSelection = -1;
		listNameRegisterFiltered.clear();
		inputNameRegister.getItems().clear();
		for (ListItemNameRegister listItemNameRegister : list0) {
			listNameRegisterFiltered.add(listItemNameRegister);
			inputNameRegister.getItems().add(listItemNameRegister.toString());
			if (listItemNameRegister.toString().contains(inputNameRegister.getEditor().getText().trim()) ||
					listItemNameRegister.getPronounce().contains(inputNameRegister.getEditor().getText().trim()))
				isCurrentInputLegal = true;
			if (previousKey.equals(listItemNameRegister.number))
				newSelection = counter;
			++counter;
		}

		if (!withoutSelect) {
			if (!isCurrentInputLegal)
				inputNameRegister.getEditor().clear();
			if (newSelection != -1) {
				inputNameRegister.getSelectionModel().clearAndSelect(newSelection);
				inputNameRegister.getEditor().setText(inputNameRegister.getItems().get(newSelection));
			}
		}
	}

	private boolean shouldSupressKeyCode(KeyCode code) {
		return code == KeyCode.DOWN ||
				code == KeyCode.UP ||
				code == KeyCode.ENTER;
	}

	@FXML
	void buttonExitClicked() throws IOException {
		Scene scene = new Scene(FXMLLoader.load(getClass().getResource("Login.fxml")));
		FXRobotHelper.getStages().get(0).setScene(scene);
	}
}

class TryRegisterService extends Service {
	String registerCategoryNumber;
	String doctorNumber;
	String patientNumber;
	double registerFee;
	boolean deductFromBalance;
	double addToBalance;
	int retry = 5;

	int registerNumber;
	RegisterException.ErrorCode returnCode;
	double updatedBalance;

	public void setRetry(int retry) {
		this.retry = retry;
	}

	TryRegisterService(
			String regCatNum,
			String docNum,
			String patientNum,
			double regFee,
			boolean deduct,
			double add) {
		registerCategoryNumber = regCatNum;
		doctorNumber = docNum;
		patientNumber = patientNum;
		registerFee = regFee;
		deductFromBalance = deduct;
		addToBalance = add;
	}

	@Override
	protected Task createTask() {
		return new Task() {
			@Override
			protected Object call() throws Exception {
				boolean retryFlag = false;
				do {
					try {
						registerNumber = DBConnector.getInstance().tryRegister(
								registerCategoryNumber,
								doctorNumber,
								patientNumber,
								registerFee,
								deductFromBalance,
								addToBalance);
					} catch (RegisterException e) {
						retryFlag = true;
						switch (e.error) {
							case sqlException:
								returnCode = RegisterException.ErrorCode.sqlException;
								break;
							case registerNumberExceeded:
							case registerCategoryNotFound:
							case patientNotExist:
								returnCode = e.error;
								return null;
						}
					}
				} while (retryFlag && --retry > 0);

				if (retry == 0)
					returnCode = RegisterException.ErrorCode.retryTimeExceeded;
				else {
					returnCode = RegisterException.ErrorCode.noError;
					try {
						ResultSet patientInfo = DBConnector.getInstance().getPatientInfo(patientNumber);
						if (!patientInfo.next())
							returnCode = RegisterException.ErrorCode.patientNotExist;
						updatedBalance = patientInfo.getDouble(Config.NameTableColumnPatientBalance);
					} catch (SQLException e) {
						returnCode = RegisterException.ErrorCode.sqlException;
						return null;
					}
				}
				return null;
			}
		};
	}
}
