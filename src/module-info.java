module R202_DASTAN_Berat {
	requires javafx.controls;
    requires javafx.fxml;

	opens application to javafx.graphics, javafx.fxml;
}
