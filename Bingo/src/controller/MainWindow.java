package controller;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class MainWindow {
    private static final Random RANDOM = new Random();

    @FXML
    private CheckBox generate;

    @FXML
    private Button resultButton;

    // Поля для комбинаций чисел
    @FXML
    private TextField user1;

    @FXML
    private TextField user2;

    @FXML
    private TextField user3;

    @FXML
    private TextField user4;

    @FXML
    private TextField user5;

    @FXML
    private TextField win1;

    @FXML
    private TextField win2;

    @FXML
    private TextField win3;

    @FXML
    private TextField win4;

    @FXML
    private TextField win5;

    private List<TextField> userFields;
    private List<TextField> wins;

    // Set для числовых комбинаций
    private final Set<Integer> winningCombination;
    private final Set<Integer> userCombination;

    public MainWindow() {
        winningCombination = new HashSet<>();
        userCombination = new HashSet<>();
    }

    @FXML
    void initialize() {
        // Создание выигрышной комбинации
        generateWinningCombination();

        // Блокировка кнопки
        resultButton.setDisable(true);

        // Инициализируем лист выигрышных
        wins = Arrays.asList(win1, win2, win3, win4, win5);

        // Инициализируем лист ввода
        userFields = Arrays.asList(user1, user2, user3, user4, user5);
        // Разрешаем вводить только цифры
        UnaryOperator<TextFormatter.Change> textFieldFilter = createTextFieldFilter();
        userFields.forEach(f -> f.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), null, textFieldFilter)));

        // Выбор генерации пользовательской комбинации
        setUpGenerateCheckBox();

        // Запрос проверки результата
        resultButton.setOnAction(event -> {
            // Демонстрация результата лотереи
            if (userCombination.size() == 5) {
                // Демонстрация выигрышной комбинации
                setWinningCombination();
                showResultWindow();
            }
        });
    }

    // Ограничение ввода чисел (от 1 до 36)
    private UnaryOperator<TextFormatter.Change> createTextFieldFilter() {
        return change -> {
            String newText = change.getControlNewText();
            if (newText.equals("")) {
                return change;
            }
            if (newText.matches("[1-9]|1[0-9]|2[0-9]|3[0-6]")) {
                if (userCombination.add(Integer.valueOf(newText)) && userCombination.size() == 5) {
                    disableUserFields();
                    resultButton.setDisable(false);
                }
                return change;
            }
            return null;
        };
    }

    // Создание выигрышной комбинации
    private Set<Integer> generateWinningCombination() {
        Set<Integer> result = new HashSet<>();
        while (result.size() != 5) {
            int number = RANDOM.nextInt(36) + 1;
            result.add(number);
        }
        return result;
    }

    // Передача выигрышной комбинации
    private void setWinningCombination() {
        Set<Integer> numbers = generateWinningCombination();
        int idx = 0;
        for (Integer number : numbers) {
            winningCombination.add(number);
            wins.get(idx).setText(number.toString());
            idx++;
        }
    }

    // Очистка выигрышной комбинации
    public void cleanWinningCombination() {
        winningCombination.clear();
        for (TextField win : wins) {
            win.setText("");
        }
    }

    // Создание пользовательской комбинации
    private Set<Integer> generateUserCombination() {
        Set<Integer> generation = new HashSet<>();
        while (generation.size() != 5) {
            int number = RANDOM.nextInt(36) + 1;
            generation.add(number);
        }
        return generation;
    }

    // Передача пользовательской комбинации
    private void setUserCombination() {
        Set<Integer> numbers = generateUserCombination();
        int idx = 0;
        for (Integer number : numbers) {
            userCombination.add(number);
            userFields.get(idx).setText(number.toString());
            idx++;
        }
    }

    // Очистка пользовательской комбинации
    private void cleanUserCombination() {
        userCombination.clear();
        for (TextField usersField : userFields) {
            usersField.setText("");
        }
    }

    // Автоматическое заполнение пользовательской комбинации
    private void setUpGenerateCheckBox() {
        generate.setOnAction(event -> {
            if (generate.isSelected()) {
                generateUserCombination();
                setUserCombination();
                disableUserFields();
                resultButton.setDisable(false);
            } else {
                cleanUserCombination();
                enableUserFields();
                resultButton.setDisable(true);
            }
        });
    }

    private void enableUserFields() {
        for (TextField userField : userFields) {
            userField.setDisable(false);
        }
    }

    private void disableUserFields() {
        for (TextField userField : userFields) {
            userField.setDisable(true);
        }
    }

    // Открытие окна с результатами
    private void showResultWindow() {
        try {
            FXMLLoader resultWindowLoader = new FXMLLoader();
            resultWindowLoader.setLocation(getClass().getResource("/sample/resultWindow.fxml"));
            Stage secondaryStage = new Stage();
            Parent parent = resultWindowLoader.load();
            Scene scene = new Scene(parent, 400, 200);
            secondaryStage.setScene(scene);
            secondaryStage.setResizable(false);
            secondaryStage.initModality(Modality.APPLICATION_MODAL);

            // Трансформирует коллекцию из Integer в коллекцию из String
            List<String> winningNumbers = winningCombination.stream().map(Object::toString).collect(Collectors.toList());
            String joinWins = String.join(" ", winningNumbers);
            List<String> userNumbers = userCombination.stream().map(Object::toString).collect(Collectors.toList());
            String joinUsers = String.join(" ", userNumbers);

            // Получение текста из окна с результатами
            ResultWindow resultWindow = resultWindowLoader.getController();

            // Установка названия и текста
            if (userCombination.equals(winningCombination)) {
                secondaryStage.setTitle("Поздравляем!");
                resultWindow.setResultText("Поздравляем, вы выиграли!\n" +
                        "Ваша комбинация: " + joinUsers + "\nВыигрышная комбинация: " + joinWins);
            } else {
                secondaryStage.setTitle("Увы...");
                resultWindow.setResultText("К сожалению, вы проиграли...\n" +
                        "Ваша комбинация: " + joinUsers + "\nВыигрышная комбинация: " + joinWins);
            }

            secondaryStage.show();

            secondaryStage.setOnCloseRequest(event -> {
                cleanUserCombination();
                cleanWinningCombination();
                generate.setSelected(false);
                enableUserFields();
                resultButton.setDisable(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}