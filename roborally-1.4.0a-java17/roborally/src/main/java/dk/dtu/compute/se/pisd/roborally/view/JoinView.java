package dk.dtu.compute.se.pisd.roborally.view;

import java.util.ArrayList;
import java.util.List;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.HttpController;
import dk.dtu.compute.se.pisd.roborally.model.Lobby;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class JoinView extends VBox implements ViewObserver {

    private boolean notHost;

    List<Button> places;
    private Button Refresh;

    VBox vbox;

    public JoinView(boolean notHost){
        this.notHost = notHost;

        places = new ArrayList<>();

        // Add four buttons to the list
//        for (int i = 1; i <= 4; i++) {
//            places.add(new Button("Button " + i));
//        }

        List<Lobby> list = new ArrayList<>();
        try {
            List<Lobby> f = HttpController.getLobbies();
            list.addAll(f);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(list.size());

        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).getLobbyID());
//            places.add(new Button(list.get(i).getLobbyID() + ", " + list.get(i).getCurrentPlayerCount() + "/" + list.get(i).getMaxPlayerCount()));
        }

        Refresh = new Button("Refresh");
        Refresh.setOnAction(e -> {
            System.out.println("refreshing the lobby");
        });

        vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setSpacing(10);

        
        vbox.getChildren().addAll(places);
        vbox.getChildren().add(Refresh);
        this.getChildren().add(vbox);
    }




    @Override
    public void updateView(Subject subject) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateView'");
    }
    
}
