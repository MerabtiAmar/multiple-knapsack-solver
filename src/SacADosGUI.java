import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class SacADosGUI extends JFrame {

    private final JPanel heuristiquePanel;
    private final JPanel objValPanel;
    private final JPanel objPoidsPanel;
    private final JPanel sacsCapPanel;
    private final JPanel heuristiqueDesc;
    private final JPanel instancePanel;
    private final JPanel actionButtons;
    private static JPanel topRightPanel;

    private final JPanel genetiquePanel;
    private final JPanel bsoPanel;
    private JTextField[] genetiqueParams;
    private JTextField[] bsoParams;

    private JTextArea descriptionTextArea;
    public static JTextField[][] outputSolution;

    private JComboBox<String> listHeuristique;
    static JTextArea console;

    JComboBox<String> listAlgo;
    JTextField nb_objet_text;
    JTextField nb_sac_text;

    JTextField[] val;
    JTextField[] poids;
    JTextField[] cap;

    static int nb_objet = -1;
    static int nb_sac = -1;

    static Integer[][] objets;
    static Integer[] sacs;

    Integer[] solution;

    public SacADosGUI() {
        super("Sac a Dos Multiple");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setResizable(false);

        int width = 1024;
        int height = 720;

        setSize(width, height);

        JPanel contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        //left Panel
        JPanel leftPanel = new JPanel();
        leftPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JPanel algoPanel = new JPanel(new GridLayout(1, 2));

        JLabel labelAlgo = new JLabel("Choisir un algorithme :");
        labelAlgo.setFont(labelAlgo.getFont().deriveFont(Font.BOLD, 14f));
        labelAlgo.setPreferredSize(new Dimension(labelAlgo.getPreferredSize().width + 20, 30));
        algoPanel.add(labelAlgo);

        String[] algorithms = {"DFS", "BFS", "A*", "Algorithme génétique","BSO"};
        listAlgo = new JComboBox<>(algorithms);
        listAlgo.setFont(listAlgo.getFont().deriveFont(Font.BOLD, 14f));
        listAlgo.setPreferredSize(new Dimension(150, 30));
        algoPanel.add(listAlgo);

        leftPanel.add(algoPanel);

        heuristiquePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(heuristiquePanel);

        genetiquePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(genetiquePanel);

        bsoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(bsoPanel);

        JPanel validateInstance = new JPanel(new FlowLayout());

        JPanel getNumber = new JPanel(new GridLayout(2, 2));

        nb_objet_text = new JTextField();
        nb_objet_text.add(new JLabel("nombre d'objets : "));
        nb_objet_text.setPreferredSize(new Dimension(100, 30));
        nb_objet_text.setFont(nb_objet_text.getFont().deriveFont(Font.BOLD, 14f));
        addDocumentFilter(nb_objet_text);

        JLabel labelObjet = new JLabel("Nombre d'objets : ");
        labelObjet.setFont(labelObjet.getFont().deriveFont(Font.BOLD, 14f));
        labelObjet.setPreferredSize(new Dimension(labelObjet.getPreferredSize().width + 50, 30));
        getNumber.add(labelObjet);

        getNumber.add(nb_objet_text);

        nb_sac_text = new JTextField();
        nb_sac_text.add(new JLabel("nombre de sacs : "));
        nb_sac_text.setPreferredSize(new Dimension(100, 30));
        nb_sac_text.setFont(nb_sac_text.getFont().deriveFont(Font.BOLD, 14f));
        addDocumentFilter(nb_sac_text);


        JLabel labelSac = new JLabel("Nombre de sacs : ");
        labelSac.setFont(labelSac.getFont().deriveFont(Font.BOLD, 14f));
        labelSac.setPreferredSize(new Dimension(labelSac.getPreferredSize().width + 50, 30));
        getNumber.add(labelSac);

        getNumber.add(nb_sac_text);

        leftPanel.add(getNumber);
        JButton valider = new JButton("Valider");
        valider.setFont(valider.getFont().deriveFont(Font.BOLD,14f));
        validateInstance.add(valider);
        leftPanel.add(validateInstance);


        objValPanel = new JPanel(new GridLayout());
        leftPanel.add(objValPanel);

        objPoidsPanel = new JPanel(new GridLayout());
        leftPanel.add(objPoidsPanel);

        sacsCapPanel = new JPanel(new GridLayout());
        leftPanel.add(sacsCapPanel);

        instancePanel = new JPanel(new GridLayout(1,2));
        JButton generateRandomButton = new JButton("Générer aléatoirement");
        generateRandomButton.setFont(generateRandomButton.getFont().deriveFont(Font.BOLD, 14f));
        JButton valideValeur = new JButton("Valider les valeurs");
        valideValeur.setFont(valideValeur.getFont().deriveFont(Font.BOLD,14f));
        leftPanel.add(instancePanel);

        actionButtons = new JPanel(new GridLayout(1, 3,10,0));
        JButton run = new JButton("Run");
        run.setFont(run.getFont().deriveFont(Font.BOLD,14f));
        JButton stop = new JButton("Stop");
        stop.setFont(stop.getFont().deriveFont(Font.BOLD,14f));
        JButton clear = new JButton("Clear");
        clear.setFont(clear.getFont().deriveFont(Font.BOLD,14f));
        leftPanel.add(actionButtons,BorderLayout.SOUTH);


        heuristiqueDesc = new JPanel(new GridLayout(1,1));
        contentPane.add(heuristiqueDesc,BorderLayout.SOUTH);

        run.addActionListener(e ->{
            if(check(objets,sacs)) {
                setButtonClickable(run, clear, generateRandomButton, valider, valideValeur, false);
                Thread algorithmThread = new Thread(() -> {
                    String algorithm = (String) listAlgo.getSelectedItem();
                    if (Objects.equals(algorithm, "DFS")) {
                        console.append("   DFS is running please wait...\n");
                        SacADosMultiple.stDFS = System.nanoTime();
                        solution = SacADosMultiple.DFS(sacs, objets);
                        SacADosMultiple.etDFS = System.nanoTime();
                        console.append(SacADosMultiple.afficherSol("DFS",
                                null,
                                String.valueOf((SacADosMultiple.etDFS - SacADosMultiple.stDFS) / 1e9),null,
                                solution, SacADosMultiple.noeudsDFS, objets));
                    } else if (Objects.equals(algorithm, "BFS")) {
                        console.append("   BFS is running please wait...\n");
                        SacADosMultiple.stBFS = System.nanoTime();
                        solution = SacADosMultiple.BFS(sacs, objets);
                        SacADosMultiple.etBFS = System.nanoTime();
                        console.append(SacADosMultiple.afficherSol("BFS",
                                null,
                                String.valueOf((SacADosMultiple.etBFS - SacADosMultiple.stBFS) / 1e9),null,
                                solution, SacADosMultiple.noeudsBFS, objets));
                    } else if (Objects.equals(algorithm, "A*")) {
                        console.append("   A* is running please wait...\n");
                        int typeHeuristique = listHeuristique.getSelectedIndex() + 1;
                        SacADosMultiple.stA = System.nanoTime();
                        solution = SacADosMultiple.Aetoil(sacs, objets, typeHeuristique);
                        SacADosMultiple.etA = System.nanoTime();
                        console.append(SacADosMultiple.afficherSol("A*",
                                String.valueOf(typeHeuristique),
                                String.valueOf((SacADosMultiple.etA - SacADosMultiple.stA) / 1e9),null,
                                solution, SacADosMultiple.noeudsA, objets));
                    } else if(Objects.equals(algorithm, "Algorithme génétique")){
                        console.append("   Algorithme généntique is running please wait...\n");
                        double[] params = getGenetiqueParams();
                        GA.start = System.nanoTime();
                        try {
                            solution = (Objects.requireNonNull(GA.GeneticAlgorithm(
                                    (int) params[0], (int) params[1], (int) params[2], (int) params[3], params[4], nb_objet, nb_sac, objets, sacs))).sol;
                        }catch (NullPointerException ignored){

                        }

                        GA.end = System.nanoTime();
                        console.append(SacADosMultiple.afficherSol(
                                "Algorithme génétique",
                                String.valueOf((GA.end - GA.start)/1e9),
                                params,
                                solution,
                                objets
                                )
                        );
                    } else if(Objects.equals(algorithm, "BSO")){
                        console.append("   BSO is running please wait...\t");
                        int[] params = getBSOParams();
                        try {
                            BSO.max_iter = params[0];
                            BSO.flip = params[1];
                            BSO.nb_bees = params[2];
                            BSO.max_chances = params[3];
                            BSO.nb_chances = params[3];
                            BSO.local_iter = params[4];
                            BSO.nb_objets = nb_objet;
                            BSO.nb_sacs = nb_sac;
                            BSO.objets = objets;
                            BSO.sacs = sacs;
                            BSO.start = System.nanoTime();
                            solution = BSO.beeSwarmOptimization(objets,sacs);
                            BSO.end = System.nanoTime();
                        }catch (NullPointerException ignored){

                        }
                        console.append(SacADosMultiple.afficherSol(
                                        "BSO",
                                        null,
                                        String.valueOf((BSO.end - BSO.start)/1e9),
                                        params,
                                        solution,
                                        0,objets
                                )
                        );
                    }
                    setButtonClickable(run, clear, generateRandomButton, valider, valideValeur, true);
                    solutionToGrid(solution,outputSolution);
                });

                algorithmThread.start();

                stop.addActionListener(stopEvent -> {
                    algorithmThread.interrupt();
                    setButtonClickable(run, clear, generateRandomButton, valider, valideValeur, true);
                    console.append("   Processus arreté par l'utilisateur\n");
                });
            } else {
                console.append("   Veuillez vérifier que les objets (valeurs,poids) et les sacs (capacités) sont bien remplis!\n");
            }
        });



        // right panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        //top right panel
        topRightPanel = new JPanel(new BorderLayout());

        //bottom right panel
        JPanel bottomRightPanel = new JPanel();
        bottomRightPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        console = new JTextArea();
        console.setEditable(false);
        console.setLineWrap(true);
        console.setWrapStyleWord(true);
        console.setFont(console.getFont().deriveFont(Font.BOLD, 14f));
        console.setBackground(new Color(32, 33, 32));
        console.setForeground(new Color(0, 190, 10));

        JScrollPane scrollPane = new JScrollPane(console);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // Always show vertical scrollbar
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Never show horizontal scrollbar

        scrollPane.setPreferredSize(new Dimension(width - width/3, height/4));

        DefaultCaret caret = (DefaultCaret) console.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);


        bottomRightPanel.add(scrollPane);

        centerPanel.add(topRightPanel, BorderLayout.CENTER);
        centerPanel.add(bottomRightPanel, BorderLayout.SOUTH);

        leftPanel.setPreferredSize(new Dimension(width / 3 + 50, height - height/4));

        centerPanel.setPreferredSize(new Dimension(width - width / 3, height));

        int centerHeight = centerPanel.getPreferredSize().height;
        int topRightHeight = centerHeight * 3 / 4;
        int bottomRightHeight = centerHeight - topRightHeight;

        topRightPanel.setPreferredSize(new Dimension(centerPanel.getPreferredSize().width, topRightHeight));
        bottomRightPanel.setPreferredSize(new Dimension(centerPanel.getPreferredSize().width, bottomRightHeight));

        contentPane.add(leftPanel, BorderLayout.WEST);
        contentPane.add(centerPanel, BorderLayout.CENTER);

        listAlgo.addActionListener(e -> {
            if (Objects.equals(listAlgo.getSelectedItem(), "A*")) {
                removeGenetiqueBox();
                removeBSOBox();
                addHeuristiqueBox();
            }else if(Objects.equals(listAlgo.getSelectedItem(), "Algorithme génétique")) {
                removeHeuristiqueBox();
                removeBSOBox();
                addGenetiqueBox();
            }else if (Objects.equals(listAlgo.getSelectedItem(), "BSO")){
                removeBSOBox();
                removeHeuristiqueBox();
                removeGenetiqueBox();
                addBSOBox();
            }else {
                removeHeuristiqueBox();
                removeGenetiqueBox();
            }
        });

        valider.addActionListener(e ->{
            if(!Objects.equals(nb_objet_text.getText(), "") && !Objects.equals(nb_sac_text.getText(), "")) {
                nb_objet = Integer.parseInt(nb_objet_text.getText());
                objValPanel.setLayout(new GridLayout(1, nb_objet));
                val = objectsValeursFeilds(nb_objet, width);
                objPoidsPanel.setLayout(new GridLayout(1, nb_objet));
                poids = objectsPoidsFeilds(nb_objet, width);
                nb_sac = Integer.parseInt(nb_sac_text.getText());
                sacsCapPanel.setLayout(new GridLayout(1, nb_sac));
                cap = sacsCapacitesFeilds(nb_sac, width);
                instancePanel.add(generateRandomButton);
                instancePanel.add(valideValeur);
                actionButtons.add(run);
                actionButtons.add(stop);
                actionButtons.add(clear);
                objets = new Integer[2][nb_objet];
                sacs = new Integer[nb_sac];
                topRightPanel.removeAll();
                outputSolution = createGridPanel(topRightPanel,nb_objet, nb_sac);
                solution = new Integer[nb_objet];
                Arrays.fill(solution,-1);
                solutionToGrid(solution,outputSolution);
            } else {
                console.append("   Veuillez donner le nombre d'objets et le nombre de sacs!\n");
            }
        });


        generateRandomButton.addActionListener(e -> generateRandomValues());
        valideValeur.addActionListener(e -> validerValeur());


        clear.addActionListener(e -> clearFields());

        pack();
        setVisible(true);
    }

    private double[] getGenetiqueParams() {
        double[] params = new double[5];
        if(genetiqueParams != null){
            for (int i = 0; i < 5; i++) {
                if(i != 4)
                    params[i] = Integer.parseInt(genetiqueParams[i].getText());
                else
                    params[i] = Double.parseDouble(genetiqueParams[i].getText());
            }
        }
        return params;
    }

    private int[] getBSOParams() {
        int[] params = new int[5];
        if(bsoParams != null){
            for (int i = 0; i < 5; i++) {
                params[i] = Integer.parseInt(bsoParams[i].getText());
            }
        }
        return params;
    }


    private static void setButtonClickable(JButton run, JButton clear, JButton generateRandomButton, JButton valider, JButton valideValeur, boolean clickable) {
        run.setEnabled(clickable);
        clear.setEnabled(clickable);
        generateRandomButton.setEnabled(clickable);
        valider.setEnabled(clickable);
        valideValeur.setEnabled(clickable);
    }


    private boolean check(Integer[][] objets, Integer[] sacs){
        if(objets == null || sacs == null) return false;
        for(int i = 0;i<objets[0].length;i++){
            if(objets[0][i] == null || objets[1][i] == null) return false;
        }
        for (Integer sac : sacs) {
            if (sac == null) return false;
        }
        return true;
    }

    private void addHeuristiqueBox() {
        if (heuristiquePanel.getComponentCount() == 0) {
            heuristiquePanel.setLayout(new GridLayout(1, 2));

            JLabel label = new JLabel("Choisir une heuristique : ");
            label.setFont(label.getFont().deriveFont(Font.BOLD, 14f));
            heuristiquePanel.add(label);
            String[] items = {"Heuristique 1", "Heuristique 2", "Heuristique 3"};
            listHeuristique = new JComboBox<>(items);
            listHeuristique.setFont(listHeuristique.getFont().deriveFont(Font.BOLD, 14f));
            heuristiquePanel.add(listHeuristique);
            heuristiquePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            descriptionTextArea = new JTextArea();
            descriptionTextArea.setEditable(false);
            descriptionTextArea.setLineWrap(true);
            descriptionTextArea.setWrapStyleWord(true);
            descriptionTextArea.setFont(descriptionTextArea.getFont().deriveFont(Font.BOLD, 14f));
            descriptionTextArea.setAlignmentX(Component.CENTER_ALIGNMENT);
            heuristiqueDesc.add(descriptionTextArea);
            heuristiqueDesc.setBorder(BorderFactory.createEmptyBorder(0,5,5,0));
            heuristiqueDesc.setBackground(Color.WHITE);


            String[] descHeuristique = {
                            "Heuristique 1 : Calcule le rapport entre la somme des valeurs des objets non placés et la capacité restante des sacs," +
                            " favorisant les solutions où les objets de haute valeur sont placés dans des sacs avec une capacité" +
                            " restante plus élevée.",

                            "Heuristique 2 : Utilise la relaxation linéaire pour estimer la valeur maximale en ajoutant les objets de manière" +
                            " rentable dans les sacs, où on suppose qu'on peut fractionner les objets en plusieurs petits objets, " +
                            "et les mettre dans les sacs, en prenant en compte les capacités restantes dans chaque sac.",

                            "Heuristique 3 : Pénalise la différence maximale de poids entre les sacs tout en maximisant la valeur totale des " +
                            "objets, favorisant les solutions équilibrées en termes de poids entre les sacs."
            };
            descriptionTextArea.setText(descHeuristique[0]);

            listHeuristique.addActionListener(e -> {
                int index = listHeuristique.getSelectedIndex();
                if (index >= 0 && index < descHeuristique.length) {
                    descriptionTextArea.setText(descHeuristique[index]);
                }
            });

            heuristiquePanel.revalidate();
            heuristiquePanel.repaint();

            heuristiqueDesc.revalidate();
            heuristiqueDesc.repaint();
        }
    }

    private void addGenetiqueBox(){
        if(genetiquePanel.getComponentCount() == 0){
            genetiquePanel.setLayout(new GridLayout(5,2));
            String[] labels = {
                    "Taille Population :",
                    "Nombre de générations :",
                    "Facteur de selection :",
                    "Stagnation limite :",
                    "Proba du mutation :"
            };
            genetiqueParams = new JTextField[5];
            for (int i = 0; i < 5; i++) {
                JLabel l1 = new JLabel(labels[i]);
                l1.setFont(l1.getFont().deriveFont(Font.BOLD, 14f));
                genetiquePanel.add(l1);
                JTextField feild = new JTextField();
                feild.setFont(feild.getFont().deriveFont(Font.BOLD, 14f));
                feild.setPreferredSize(new Dimension(100, 30));
                if(i != 4)
                    addDocumentFilter(feild);
                else
                    ((AbstractDocument) feild.getDocument()).setDocumentFilter(new DoubleFilter());
                genetiqueParams[i] = feild;
                genetiquePanel.add(genetiqueParams[i]);
            }
            genetiquePanel.revalidate();
            genetiquePanel.repaint();
        }
    }

    private void addBSOBox(){
        if(bsoPanel.getComponentCount() == 0){
            bsoPanel.setLayout(new GridLayout(5,2));
            String[] labels = {
                    "Nombre d'itération :",
                    "Flip :",
                    "Nombre d'abeilles :",
                    "Max Chances :",
                    "Nombre itérations locale:"
            };
            bsoParams = new JTextField[5];
            for (int i = 0; i < 5; i++) {
                JLabel l1 = new JLabel(labels[i]);
                l1.setFont(l1.getFont().deriveFont(Font.BOLD, 14f));
                bsoPanel.add(l1);
                JTextField feild = new JTextField();
                feild.setFont(feild.getFont().deriveFont(Font.BOLD, 14f));
                feild.setPreferredSize(new Dimension(100, 30));
                addDocumentFilter(feild);
                bsoParams[i] = feild;
                bsoPanel.add(bsoParams[i]);
            }
            bsoPanel.revalidate();
            bsoPanel.repaint();
        }
    }

    private void removeHeuristiqueBox() {
        heuristiquePanel.removeAll();
        heuristiquePanel.revalidate();
        heuristiquePanel.repaint();

        heuristiqueDesc.removeAll();
        heuristiqueDesc.revalidate();
        heuristiqueDesc.repaint();
    }

    private void removeGenetiqueBox(){
        genetiquePanel.removeAll();
        genetiquePanel.revalidate();
        genetiquePanel.repaint();
    }

    private void removeBSOBox(){
        bsoPanel.removeAll();
        bsoPanel.revalidate();
        bsoPanel.repaint();
    }

    private JTextField[] objectsValeursFeilds(int numFields, int width) {
        objValPanel.removeAll();
        JLabel valeur = new JLabel("Valeurs : ");
        return getjTextFields(numFields, width, valeur, objValPanel);
    }


    private JTextField[] objectsPoidsFeilds(int numFields, int width) {
        objPoidsPanel.removeAll();
        JLabel poids = new JLabel("Poids :   ");
        return getjTextFields(numFields, width, poids, objPoidsPanel);
    }


    private JTextField[] sacsCapacitesFeilds(int numFields, int width) {
        sacsCapPanel.removeAll();
        JLabel cap = new JLabel("Capacités :   ");
        return getjTextFields(numFields, width, cap, sacsCapPanel);
    }

    private JTextField[] getjTextFields(int numFields, int width, JLabel label, JPanel panel) {
        label.setFont(label.getFont().deriveFont(Font.BOLD, 14f));
        JPanel inputPanel = new JPanel(new GridLayout(1, numFields));
        inputPanel.add(label);
        JTextField[] sacsCapFields = new JTextField[numFields];
        for (int i = 0; i < numFields; i++) {
            JTextField textField = new JTextField();
            textField.setPreferredSize(new Dimension(50, 40));
            textField.setFont(textField.getFont().deriveFont(Font.BOLD, 14f));
            addDocumentFilter(textField);
            inputPanel.add(textField);
            sacsCapFields[i] = textField;
        }

        JScrollPane scrollPane = new JScrollPane(inputPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        scrollPane.setPreferredSize(new Dimension(width / 3, 60));

        panel.add(scrollPane);

        panel.revalidate();
        panel.repaint();

        return sacsCapFields;
    }

    private void generateRandomValues() {
        Random random = new Random();
        if(val != null && poids != null) {
            objets = new Integer[2][val.length];
            for (int i = 0 ;i<val.length;i++) {
                int value = random.nextInt(15 - 8) + 8;
                val[i].setText(Integer.toString(value));
                int weight = random.nextInt(15 - 8) + 8;
                poids[i].setText(Integer.toString(weight));
                objets[0][i] = value;
                objets[1][i] = weight;
            }
        }

        if(cap != null) {
            sacs = new Integer[cap.length];
            for (int i = 0;i<cap.length;i++) {
                int capacity = random.nextInt(20 - 15) + 15;
                cap[i].setText(Integer.toString(capacity));
                sacs[i] = capacity;
            }
        }
        console.append("   Génération des valeurs effectué!\n");
    }

    private void validerValeur(){
        if(val != null && poids != null) {
            objets = new Integer[2][val.length];
            for (int i = 0 ;i<val.length;i++) {
                String v = val[i].getText();
                String p = poids[i].getText();
                objets[0][i] = v.isEmpty() ? null : Integer.valueOf(v);
                objets[1][i] = p.isEmpty() ? null : Integer.valueOf(p);
            }
        }
        if(cap != null) {
            sacs = new Integer[cap.length];
            for (int i = 0;i<cap.length;i++) {
                String c = cap[i].getText();
                sacs[i] = c.isEmpty() ? null : Integer.valueOf(c);
            }
        }
        console.append("   Les valeurs sont validées avec succès!\n");
    }

    private void clearFields() {
        nb_sac_text.setText("");
        nb_objet_text.setText("");
        if (val != null) {
            for (JTextField objValueField : val) {
                objValueField.setText("");
            }
        }

        if (poids != null) {
            for (JTextField objWeightField : poids) {
                objWeightField.setText("");
            }
        }

        if (cap != null) {
            for (JTextField sacsCapField : cap) {
                sacsCapField.setText("");
            }
        }
        solution = null;
        sacs = null;
        objets = null;
        console.setText("   Toutes les valeurs sont effacées!\n");
    }

    private static JTextField[][] createGridPanel(JPanel gridPanel, int nb_objets, int nb_sacs) {
        gridPanel.removeAll();
        gridPanel.setLayout(new GridLayout(nb_objets + 2, nb_sacs + 1));
        JTextField[][] newEntries = new JTextField[nb_objets + 2][nb_sacs + 1];

        for (int i = 0; i < nb_objets + 2; i++) {
            for (int j = 0; j < nb_sacs + 1; j++) {
                JTextField emptyEntry = new JTextField(10);
                emptyEntry.setEditable(false);
                emptyEntry.setFont(emptyEntry.getFont().deriveFont(Font.BOLD,14f));
                emptyEntry.setBackground(new Color(200,200,200));
                emptyEntry.setHorizontalAlignment(JTextField.CENTER);
                emptyEntry.setBorder(BorderFactory.createLineBorder(Color.WHITE));
                if(j == 0 && i != 0){
                    emptyEntry.setText("Obj " + i);
                }
                else if(i == 0 && j != 0){
                    emptyEntry.setText("Sac " + j);
                }else if(i != 0 && i != nb_objets + 1){
                    emptyEntry.setText("0");
                    emptyEntry.setBackground(new Color(150,50,50));
                }else if(i == nb_objets + 1){
                    emptyEntry.setBackground(new Color(243, 204, 109));
                    emptyEntry.setText("0%");
                    emptyEntry.setFont(emptyEntry.getFont().deriveFont(Font.ITALIC | Font.BOLD,16));
                }
                gridPanel.add(emptyEntry);
                newEntries[i][j] = emptyEntry;
            }
        }
        newEntries[0][0].setText("Obj i \\ Sac i");
        newEntries[nb_objets + 1][0].setText("% du Sac");

        gridPanel.revalidate();
        gridPanel.repaint();
        return newEntries;
    }

    public static void solutionToGrid(Integer[] sol,JTextField[][] output){
        if(sol != null) {
            for (int i = 1; i < nb_objet + 1; i++) {
                for (int j = 1; j < nb_sac + 1; j++) {
                    output[i][j].setBackground(new Color(150, 50, 50));
                    output[i][j].setText("0");
                }
            }
            for (int i = 0; i < nb_objet; i++) {
                if (sol[i] != -1) {
                    output[i + 1][sol[i] + 1].setText("1");
                    output[i + 1][sol[i] + 1].setBackground(new Color(50, 150, 50));
                }
            }
            if (sacs != null && objets != null) {
                for (int i = 0; i < nb_sac; i++) {
                    int poids = 0;
                    for (int j = 0; j < nb_objet; j++) {
                        if (sol[j] == i)
                            poids += objets[1][j];
                    }
                    DecimalFormat df = new DecimalFormat("#.##");
                    if (sacs[i] != null) {
                        float percent = ((float) poids / sacs[i]) * 100;
                        output[nb_objet + 1][i + 1].setText(df.format(percent) + "%");
                    } else {
                        output[nb_objet + 1][i + 1].setText("0.00%");
                    }
                }
            }
        }
    }


    private void addDocumentFilter(JTextField textField) {
        Document doc = textField.getDocument();
        if (doc instanceof PlainDocument) {
            ((PlainDocument) doc).setDocumentFilter(new NumericDocumentFilter());
        }
    }

    private static class NumericDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String text, AttributeSet attrs) throws BadLocationException {
            StringBuilder sb = new StringBuilder();
            for (char ch : text.toCharArray()) {
                if (Character.isDigit(ch)) {
                    sb.append(ch);
                }else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
            super.insertString(fb, offset, sb.toString(), attrs);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (char ch : text.toCharArray()) {
                if (Character.isDigit(ch)) {
                    sb.append(ch);
                }else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
            super.replace(fb, offset, length, sb.toString(), attrs);
        }
    }

    public static class DoubleFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string,
                                 AttributeSet attr) throws BadLocationException {
            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            sb.append(doc.getText(0, doc.getLength()));
            sb.insert(offset, string);

            if (test(sb.toString())) {
                super.insertString(fb, offset, string, attr);
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }

        private boolean test(String text) {
            try {
                Double.parseDouble(text);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text,
                            AttributeSet attrs) throws BadLocationException {
            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            sb.append(doc.getText(0, doc.getLength()));
            sb.replace(offset, offset + length, text);

            if (test(sb.toString())) {
                super.replace(fb, offset, length, text, attrs);
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length)
                throws BadLocationException {
            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            sb.append(doc.getText(0, doc.getLength()));
            sb.delete(offset, offset + length);

            if (test(sb.toString())) {
                super.remove(fb, offset, length);
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }


    public static void main(String[] args) {
        new SacADosGUI();
    }
}
