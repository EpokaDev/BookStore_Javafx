package application.bookstore.views;
import java.io.FileInputStream;

import application.bookstore.charts.Chart1;
import application.bookstore.models.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AdminView extends AbstractAdminView{
	private final User user;
	private final ImageView[] buttonImages = new ImageView[5];
    private VBox top;
	private Scene scene;
    private VBox center;

    public AdminView(User user){
		this.user = user;
	}
	public Scene showView(Stage stage) {

        BorderPane frame = new BorderPane(); // Main Frame that will hold every component and pane
        //frame.setStyle("-fx-background-color: #FFF9E9;");
		Image backgroundImage;

		String osName = System.getProperty("os.name").toLowerCase();

		if (osName.contains("win"))
		{
			backgroundImage = new Image("C:\\Users\\alvin\\OneDrive\\Desktop\\SW_testing\\Bookstore_javfx_testing\\BookStore_Javafx\\BookStore\\Images\\3d-render-wooden-table-looking-out-blurred-background-with-bookcase.jpg");

		}else
		{
			backgroundImage = new Image("file:/Users/regiloshi/IdeaProjects/BookStore_Javafx/BookStore/Images/3d-render-wooden-table-looking-out-blurred-background-with-bookcase.jpg");
		}

		BackgroundSize backgroundSize = new BackgroundSize(
				1000,  // Width of the scene
				720,   // Height of the scene
				false, // Width is percentage
				false, // Height is percentage
				false, // Width contains aspect ratio
				false  // Height contains aspect ratio
		);

		BackgroundImage background = new BackgroundImage(
				backgroundImage,
				BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT,
				BackgroundPosition.DEFAULT,
				backgroundSize
		);

		frame.setBackground(new Background(background));

		getButtonImages();//call function that will get the images required for the side options


        HBox options_1 = new HBox();
        HBox options_2 = new HBox();
        VBox options_3 = new VBox();

        Button profile_button = new Button("", buttonImages[0]);
		profile_button.setPrefHeight(120);
		profile_button.setPrefWidth(150);
		profile_button.setBackground(null);


		profile_button.setOnAction(e -> {
			Stage popup = new Stage();
			ProfileView profileView = new ProfileView(user);
			popup.setScene(profileView.showView(popup));
			popup.show();
		});


        Button other_users_buttons = new Button("", buttonImages[1]);
		other_users_buttons.setPrefHeight(120);
		other_users_buttons.setPrefWidth(150);
		other_users_buttons.setBackground(null);

        Button bookstore_button = new Button("", buttonImages[2]);
		bookstore_button.setPrefHeight(120);
		bookstore_button.setPrefWidth(150);
		bookstore_button.setBackground(null);

		bookstore_button.setOnAction(e -> {
			BookView bookview = new BookView(user.getRole() , user);
			stage.setScene(bookview.showView(stage));
		});

        Button statistics_button = new Button("", buttonImages[3]);
		statistics_button.setPrefHeight(120);
		statistics_button.setPrefWidth(150);
		statistics_button.setBackground(null);

        Button log_out_button = new Button("", buttonImages[4]);
		log_out_button.setPrefHeight(120);
		log_out_button.setPrefWidth(150);
		log_out_button.setBackground(null);
		log_out_button.setOnAction(event -> {
			LoginView loginView = new LoginView();
			stage.setScene(loginView.showView(stage));
		});

		options_1.getChildren().addAll(profile_button, other_users_buttons, bookstore_button);
		options_2.getChildren().addAll(statistics_button, log_out_button);
		options_2.setSpacing(20);
		options_3.getChildren().addAll(options_1, options_2);
		options_3.setAlignment(Pos.CENTER);

		//event handlers for the buttons
		other_users_buttons.setOnAction(e->{
			OtherUsersView otherUsersView=new OtherUsersView(user);
			stage.setScene(otherUsersView.showView(stage));
		});

		statistics_button.setOnAction(e-> {
			Chart1 chart1=new Chart1(user);
			stage.setScene(chart1.showView(stage));
				}
		);

        Label welcome_user = new Label("Welcome " + user.getFirstName() + " " + user.getLastName());                                                //!!!!!!!!!!!! Replace with the class.getUsername(); to make it Dynamic depending on which users logs in
        welcome_user.setStyle(
				"-fx-font-size: 80px;" + // Set font size to 24 pixels							//!!!!!!! Not Finished yet the style will be changed to match background and look better
						"-fx-font-family: 'Arial';" + // Set font family to Arial
						"-fx-text-fill: white;" // Set text color to white
		);
		BorderPane.setAlignment(welcome_user, Pos.CENTER);
		BorderPane.setAlignment(options_3, Pos.CENTER);
		options_1.setAlignment(Pos.CENTER);
		options_2.setAlignment(Pos.CENTER);
		double buttonSize = 150.0;
		for (Button button : new Button[]{profile_button, other_users_buttons, bookstore_button, statistics_button, log_out_button}) {
			button.setPrefHeight(buttonSize);
			button.setPrefWidth(buttonSize);
			button.setBackground(null);
		}
		frame.setTop(welcome_user);
		frame.setCenter(options_3);

		// create scene

		stage.setTitle("Admin Window");
		return new Scene(frame, 1000 , 720 );
	}

	protected void getButtonImages() {
		/*image is a fileInputStream which is used to get the png files into the code,
		 then we use that to create a imageview and store it into buttonImages array of ImageViews which is latter used to get the icons for buttons
		 */
		String os = System.getProperty("os.name").toLowerCase();

        FileInputStream image;
        Alert alert;
        Image img;
        if (os.contains("win")) {
			for(int i=0; i<5; i++) {
				try {
					switch (i) {
						case 0:
							image = new FileInputStream("C:\\Users\\alvin\\OneDrive\\Desktop\\BookStoreJavafx\\BookStore\\Images\\user-3.png");
							img = new Image(image);
							buttonImages[0] = new ImageView(img);
							buttonImages[0].setFitHeight(100);
							buttonImages[0].setFitWidth(100);
							break;
						case 1:
							image = new FileInputStream("C:\\Users\\alvin\\OneDrive\\Desktop\\BookStoreJavafx\\BookStore\\Images\\group-3.png");
							img = new Image(image);
							buttonImages[1] = new ImageView(img);
							buttonImages[1].setFitHeight(100);
							buttonImages[1].setFitWidth(100);
							break;
						case 2:
							image = new FileInputStream("C:\\Users\\alvin\\OneDrive\\Desktop\\BookStoreJavafx\\BookStore\\Images\\book-3.png");
							img = new Image(image);
							buttonImages[2] = new ImageView(img);
							buttonImages[2].setFitHeight(120);
							buttonImages[2].setFitWidth(120);
							break;
						case 3:
							image = new FileInputStream("C:\\Users\\alvin\\OneDrive\\Desktop\\BookStoreJavafx\\BookStore\\Images\\trend.png");
							img = new Image(image);
							buttonImages[3] = new ImageView(img);
							buttonImages[3].setFitHeight(140);
							buttonImages[3].setFitWidth(140);
							break;
						case 4:
							image = new FileInputStream("C:\\Users\\alvin\\OneDrive\\Desktop\\BookStoreJavafx\\BookStore\\Images\\power.png");
							img = new Image(image);
							buttonImages[4] = new ImageView(img);
							buttonImages[4].setFitHeight(100);
							buttonImages[4].setFitWidth(100);
							break;
					}
				}catch(java.io.FileNotFoundException e) {
					//Alert notifying user if there is an error regarding loading picture
					//!!!!!!! Alert shows under the stage, needs to be fixed!
					alert = new Alert(AlertType.ERROR);
					alert.setContentText("Resources Missing!Contact your Admin!\nErrorCode:104");
					alert.show();
					e.fillInStackTrace();
				}
			}
		} else if (os.contains("mac")) {
			for(int i=0; i<5; i++) {
				try {
					switch(i) {
						case 0:
							image = new FileInputStream("/Users/regiloshi/IdeaProjects/BookStore_Javafx/BookStore/Images/user-3.png");
							img = new Image(image);
							buttonImages[0] = new ImageView(img);
							buttonImages[0].setFitHeight(100);
							buttonImages[0].setFitWidth(100);
							break;
						case 1:
							image = new FileInputStream("/Users/regiloshi/IdeaProjects/BookStore_Javafx/BookStore/Images/group-3.png");
							img = new Image(image);
							buttonImages[1] = new ImageView(img);
							buttonImages[1].setFitHeight(100);
							buttonImages[1].setFitWidth(100);
							break;
						case 2:
							image = new FileInputStream("/Users/regiloshi/IdeaProjects/BookStore_Javafx/BookStore/Images/book-3.png");
							img = new Image(image);
							buttonImages[2] = new ImageView(img);
							buttonImages[2].setFitHeight(120);
							buttonImages[2].setFitWidth(120);
							break;
						case 3:
							image = new FileInputStream("/Users/regiloshi/IdeaProjects/BookStore_Javafx/BookStore/Images/trend.png");
							img = new Image(image);
							buttonImages[3] = new ImageView(img);
							buttonImages[3].setFitHeight(140);
							buttonImages[3].setFitWidth(140);
							break;
						case 4:
							image = new FileInputStream("/Users/regiloshi/IdeaProjects/BookStore_Javafx/BookStore/Images/power.png");
							img = new Image(image);
							buttonImages[4] = new ImageView(img);
							buttonImages[4].setFitHeight(100);
							buttonImages[4].setFitWidth(100);
							break;
					}
				}catch(java.io.FileNotFoundException e) {
					//Alert notifying user if there is an error regarding loading picture
					//!!!!!!! Alert shows under the stage, needs to be fixed!
					alert = new Alert(AlertType.ERROR);
					alert.setContentText("Resources Missing!Contact your Admin!\nErrorCode:104");
					alert.show();
					e.fillInStackTrace();
				}
			}
		}
	}
}