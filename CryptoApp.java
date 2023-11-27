import javax.swing.*;
import java.util.Objects;

public class CryptoApp extends JFrame {
    //    шифры Вижинера, Цезаря, двойной перестановки
    private final JTextArea inputTextArea;
    private final JTextArea outputTextArea;
    private final JComboBox<String> algorithmComboBox;
    private final JTextField keyTextField;

    public CryptoApp() {
        setTitle("Simple Crypto App");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        inputTextArea = new JTextArea(5, 20);
        outputTextArea = new JTextArea(5, 20);
        algorithmComboBox = new JComboBox<>(new String[]{"шифр Виженера", "шифр Цезаря", "шифр двойной перестановки"});
        keyTextField = new JTextField(20);
        JButton encryptButton = new JButton("Шифрование");
        JButton decryptButton = new JButton("Расшифрование");

        // Запуск шифрования
        encryptButton.addActionListener(e -> {
            String inputText = inputTextArea.getText();
            String algorithm = Objects.requireNonNull(algorithmComboBox.getSelectedItem()).toString();
            String key = keyTextField.getText();
            if (key.isEmpty()) {
                outputTextArea.setText("Вы забыли ввести код");
            } else if (inputText.isEmpty()) {
                outputTextArea.setText("Вы забыли текст для кодирования");
            } else {
                switch (algorithm) {
                    case "шифр Виженера":
                        if (!key.matches("^[А-Яа-яЁё ]+$")) {
                            outputTextArea.setText("Некорректный ввод ключа. Можно вводить только русские буквы и пробел");
                        } else {
                            outputTextArea.setText(encryptVigenere(inputText.toLowerCase(), key.toLowerCase()));
                        }
                        break;
                    case "шифр Цезаря":
                        if (!key.matches("\\d+")) {
                            outputTextArea.setText("Некорректный ввод ключа. Можно вводить только цифры");
                        } else {
                            outputTextArea.setText(encryptCaesar(inputText.toLowerCase(), key));
                        }
                        break;
                    case "шифр двойной перестановки":
                        String[] parts = key.split(" ");
                        if (parts.length == 2) {
                            if (!parts[0].matches("\\d+") || !parts[1].matches("\\d+")) {
                                outputTextArea.setText("Некорректный ввод ключа. Введите только числа, разделенные пробелом.");
                                break;
                            }

                            int key1 = Integer.parseInt(parts[0]);

                            int key2 = Integer.parseInt(parts[1]);

                            outputTextArea.setText(encryptDoubleTransposition(inputText, key1, key2));
                        } else {
                            outputTextArea.setText("Некорректный ввод. Введите два числа, разделенных пробелом.");
                        }
                        break;
                    default:
                        outputTextArea.setText("Неверный выбор криптоалгоритма.");
                }
            }
        });

        // Запуск расшифрования
        decryptButton.addActionListener(e -> {
            String inputText = inputTextArea.getText();
            String algorithm = Objects.requireNonNull(algorithmComboBox.getSelectedItem()).toString();
            String key = keyTextField.getText();

            if (key.isEmpty()) {
                outputTextArea.setText("Вы забыли ввести код");
            } else if (inputText.isEmpty()) {
                outputTextArea.setText("Вы забыли текст для кодирования");
            } else {
                switch (algorithm) {
                    case "шифр Виженера":
                        if (!key.matches("^[А-Яа-яЁё ]+$")) {
                            outputTextArea.setText("Некорректный ввод. Можно вводить только русские буквы и пробел");
                        } else {
                            outputTextArea.setText(decryptVigenere(inputText, key));
                        }
                        break;
                    case "шифр Цезаря":
                        if (!key.matches("\\d+")) {
                            outputTextArea.setText("Некорректный ввод. Можно вводить только цифры");
                        } else {
                            outputTextArea.setText(decryptCaesar(inputText, key));
                        }
                        break;
                    case "шифр двойной перестановки":
                        String[] parts = key.split(" ");
                        if (parts.length == 2) {
                            if (!parts[0].matches("\\d+") || !parts[1].matches("\\d+")) {
                                outputTextArea.setText("Некорректный ввод. Введите только числа, разделенные пробелом.");
                                break;
                            }
                            int[] key1Arr = intToArray(Integer.parseInt(parts[0]));
                            int[] key2Arr = intToArray(Integer.parseInt(parts[1]));
                            outputTextArea.setText(decryptDoubleTransposition(inputText, key1Arr, key2Arr));
                        } else {
                            outputTextArea.setText("Некорректный ввод. Введите два числа, разделенных пробелом.");
                        }
                        break;
                    default:
                        outputTextArea.setText("Неверный выбор криптоалгоритма.");
                }
            }
        });

        panel.add(new JLabel("Введите текст:"));
        panel.add(new JScrollPane(inputTextArea));
        panel.add(new JLabel("Выбор алгоритма:"));
        panel.add(algorithmComboBox);
        panel.add(new JLabel("Ключ:"));
        panel.add(keyTextField);
        panel.add(encryptButton);
        panel.add(decryptButton);
        panel.add(new JLabel("Итоговый текст:"));
        panel.add(new JScrollPane(outputTextArea));
        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CryptoApp().setVisible(true));
    }

    private String encryptVigenere(String text, String key) {
        String alp = Alphabet.alphabet;

        StringBuilder retText = new StringBuilder();
        int j = 0; //для шифра отдельный счетчик, чтобы при пробелах не сбивался
        for (int i = 0; i < text.length(); i++) {
            char textChar = text.charAt(i);
            char keyChar = key.charAt(j % key.length());
            int textIndex = alp.indexOf(textChar);

            // Если символ не найден в алфавите, добавляем его без изменений
            if (textIndex == -1) {
                retText.append(textChar);
                continue;
            }

            int keyIndex = alp.indexOf(keyChar);
            int encryptedIndex = (textIndex + keyIndex) % 33;
            char encryptedChar = alp.charAt(encryptedIndex);
            retText.append(encryptedChar);
            j++;
        }
        return retText.toString();
    }


    private String decryptVigenere(String text, String key) {
        String alp = Alphabet.alphabet;
        StringBuilder decryptedText = new StringBuilder();
        // Цикл по декодируемому тексту
        int j = 0; //для шифра отдельный счетчик, чтобы при пробелах не сбивался
        for (int i = 0; i < text.length(); i++) {
            // берем буквы
            char textChar = text.charAt(i);
            char keyChar = key.charAt(j % key.length());
            // берем индексы букв
            int textIndex = alp.indexOf(textChar);
            int keyIndex = alp.indexOf(keyChar);
            // Если символ не найден в алфавите, добавляем его без изменений
            if (textIndex == -1) {
                decryptedText.append(textChar);
                continue;
            }
            j++;
            // Вычисление индекса и сама буква из алфавита по нему
            int decryptedIndex = (textIndex - keyIndex + 33) % 33;
            char decryptedChar = alp.charAt(decryptedIndex);
            decryptedText.append(decryptedChar);
        }

        return decryptedText.toString();
    }

    // шифрование Цезарем
    private static String encryptCaesar(String text, String key) {
        String alp = Alphabet.lowAlphabet;
        int iKey = Integer.parseInt(key);  // iKey - для смещения
        StringBuilder result = new StringBuilder();

        for (char c : text.toCharArray()) {
            int index = alp.indexOf(c); // Получение индекса текущего символа
            if (index != -1) {
                result.append(alp.charAt((index + iKey) % alp.length()));
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    //Обратное шифрование с помощью обратного сдвига
    private String decryptCaesar(String text, String key) {
        int shift = Integer.parseInt(key);
        String buff_key = String.valueOf(33 - shift);
        return encryptCaesar(text, buff_key);
    }

    private String decryptDoubleTransposition(String text, int[] line, int[] col) {
        char[][] matrix = new char[line.length][col.length];
        int index = 0;

        StringBuilder decryptStr = new StringBuilder();
        // Заполнение матрицы по строкам из строки исходной
        for (int j = 0; j < col.length; j++) {
            for (int i = 0; i < line.length; i++) {
                matrix[i][j] = text.charAt(index);
                index++;
            }
        }

        // Заполнение матрицы по строкам обращаемся к элементам по правильным индексам
        for (int j = 0; j < col.length; j++) {
            for (int i = 0; i < line.length; i++) {
                decryptStr.append(matrix[line[i] - 1][col[j] - 1]);
            }
        }
        return new String(decryptStr);
    }

    private String encryptDoubleTransposition(String text, int key1, int key2) {
        // key1Arr - столбцы, key2Arr - строки
        int[] key1Arr = intToArray(key1);
        int[] key2Arr = intToArray(key2);
        StringBuilder encryptStr = new StringBuilder();
        char[][] matrix = createMatrix(text, key1Arr, key2Arr);

        for (int i = 0; i < matrix[0].length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                encryptStr.append(matrix[j][i]);
            }
        }
        return encryptStr.toString();
    }

    private static char[][] createMatrix(String text, int[] line, int[] col) {
        char[][] matrix = new char[line.length][col.length];
        int index = 0;

        // Заполнение матрицы по строкам
        for (int j = 0; j < col.length; j++) {
            for (int i = 0; i < line.length; i++) {
                if (index < text.length()) {
                    matrix[line[i] - 1][col[j] - 1] = text.charAt(index);
                    index++;
                } else {
                    matrix[line[i] - 1][col[j] - 1] = ' '; // Заполнение пустых мест символом ' '
                }
            }
        }
        return matrix;
    }

    public static int[] intToArray(int number) {
        String numberString = Integer.toString(number);
        int[] result = new int[numberString.length()];
        for (int i = 0; i < numberString.length(); i++) {
            result[i] = Character.getNumericValue(numberString.charAt(i));
        }
        return result;
    }

    public static class Alphabet {
        public static String alphabet = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
        public static String lowAlphabet = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
    }
}
