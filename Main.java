import java.util.*;
import java.io.*;

public class Main {

    private static final String[] DATAS_DISPONIVEIS = {
        "29/05/2025", "30/05/2025", "03/06/2025", "04/06/2025", "05/06/2025",
        "06/06/2025", "09/06/2025", "10/06/2025", "11/06/2025", "12/06/2025"
    };

    private static final String[] HORARIOS_DISPONIVEIS = {
        "8:00", "9:00", "10:00", "11:00", "14:00", "15:00", "16:00", "17:00"
    };

    private static final String[] CONTATOS_EMERGENCIA = {
        "CVV: Ligue 188 (24 horas)",
        "Procure o CAPS mais próximo",
        "Agende uma consulta com psicólogo/psiquiatra"
    };

    private static final String ARQ_MEDICOS = "login_medicos.txt";
    private static final String ARQ_PACIENTES = "login_pacientes.txt";
    private static final String ARQ_PRONTUARIOS = "prontuarios.txt";

    private static final int MAX_MEDICOS = 10;
    private static final int MAX_PACIENTES = 100;
    private static final int MAX_PRONTUARIOS = 500;

    private static String[] loginsMedicos = new String[MAX_MEDICOS];
    private static String[] senhasMedicos = new String[MAX_MEDICOS];
    private static int numMedicos = 0;

    private static String[] loginsPacientes = new String[MAX_PACIENTES];
    private static String[] senhasPacientes = new String[MAX_PACIENTES];
    private static int numPacientes = 0;

    // Matriz para prontuários: [ID_Paciente, Nome, Data, Sintomas]
    private static String[][] prontuarios = new String[MAX_PRONTUARIOS][4];
    private static int numProntuarios = 0;

    // Credenciais do administrador
    private static final String ADMIN_LOGIN = "admin";
    private static final String ADMIN_SENHA = "admin123";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        carregarMedicos();
        carregarPacientes();
        carregarProntuarios();

        boolean sistemaAtivo = true;

        while (sistemaAtivo) {
            System.out.println("\n--- A Saúde na Palma da Mão ---");
            System.out.println("1. Login Médico");
            System.out.println("2. Login Paciente");
            System.out.println("3. Cadastrar Novo Paciente");
            System.out.println("4. Login Administrador");
            System.out.println("5. Sair");
            System.out.print("Escolha: ");
            int opcao = scanner.nextInt();

            switch (opcao) {
                case 1 -> fazerLoginMedico(scanner);
                case 2 -> fazerLoginPaciente(scanner);
                case 3 -> cadastrarNovoPaciente(scanner);
                case 4 -> fazerLoginAdministrador(scanner);
                case 5 -> {
                    sistemaAtivo = false;
                    System.out.println("Sistema encerrado.");
                }
                default -> System.out.println("Opção inválida!");
            }
        }

        scanner.close();
    }

    private static void cadastrarNovoPaciente(Scanner scanner) {
        if (numPacientes >= MAX_PACIENTES) {
            System.out.println("Limite máximo de pacientes atingido!");
            return;
        }
        
        System.out.println("\n--- CADASTRO DE NOVO PACIENTE ---");
        System.out.print("Escolha um login: ");
        String novoLogin = scanner.next();
        
        // Verificar se login já existe
        for (int i = 0; i < numPacientes; i++) {
            if (loginsPacientes[i].equals(novoLogin)) {
                System.out.println("Este login já está em uso!");
                return;
            }
        }
        
        System.out.print("Crie uma senha: ");
        String novaSenha = scanner.next();
        
        System.out.print("Confirme a senha: ");
        String confirmacaoSenha = scanner.next();
        
        if (!novaSenha.equals(confirmacaoSenha)) {
            System.out.println("As senhas não coincidem!");
            return;
        }
        
        // Adicionar novo paciente
        loginsPacientes[numPacientes] = novoLogin;
        senhasPacientes[numPacientes] = novaSenha;
        numPacientes++;
        salvarPacientes();
        
        System.out.println("\nCadastro realizado com sucesso!");
        System.out.println("Bem-vindo ao sistema, " + novoLogin + "!");
        
        // Opção para criar primeiro prontuário
        System.out.print("\nDeseja criar seu primeiro prontuário agora? (S/N): ");
        String resposta = scanner.next();
        
        if (resposta.equalsIgnoreCase("S")) {
            criarProntuarioPaciente(scanner, novoLogin);
        }
    }

    private static void carregarMedicos() {
        try (Scanner arqScanner = new Scanner(new File(ARQ_MEDICOS))) {
            while (arqScanner.hasNextLine() && numMedicos < MAX_MEDICOS) {
                String[] linha = arqScanner.nextLine().split(";");
                if (linha.length == 2) {
                    loginsMedicos[numMedicos] = linha[0];
                    senhasMedicos[numMedicos] = linha[1];
                    numMedicos++;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo de médicos não encontrado. Criando novo...");
        }
    }

    private static void salvarMedicos() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARQ_MEDICOS))) {
            for (int i = 0; i < numMedicos; i++) {
                writer.println(loginsMedicos[i] + ";" + senhasMedicos[i]);
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar médicos: " + e.getMessage());
        }
    }

    private static void carregarPacientes() {
        try (Scanner arqScanner = new Scanner(new File(ARQ_PACIENTES))) {
            while (arqScanner.hasNextLine() && numPacientes < MAX_PACIENTES) {
                String[] linha = arqScanner.nextLine().split(";");
                if (linha.length == 2) {
                    loginsPacientes[numPacientes] = linha[0];
                    senhasPacientes[numPacientes] = linha[1];
                    numPacientes++;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo de pacientes não encontrado. Criando novo...");
        }
    }

    private static void salvarPacientes() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARQ_PACIENTES))) {
            for (int i = 0; i < numPacientes; i++) {
                writer.println(loginsPacientes[i] + ";" + senhasPacientes[i]);
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar pacientes: " + e.getMessage());
        }
    }

    private static void carregarProntuarios() {
        try (Scanner arqScanner = new Scanner(new File(ARQ_PRONTUARIOS))) {
            while (arqScanner.hasNextLine() && numProntuarios < MAX_PRONTUARIOS) {
                String[] dados = arqScanner.nextLine().split(";");
                if (dados.length == 4) {
                    prontuarios[numProntuarios][0] = dados[0];
                    prontuarios[numProntuarios][1] = dados[1];
                    prontuarios[numProntuarios][2] = dados[2];
                    prontuarios[numProntuarios][3] = dados[3];
                    numProntuarios++;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo de prontuários não encontrado. Criando novo...");
        }
    }

    private static void salvarProntuarios() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARQ_PRONTUARIOS))) {
            for (int i = 0; i < numProntuarios; i++) {
                writer.println(
                    prontuarios[i][0] + ";" + 
                    prontuarios[i][1] + ";" + 
                    prontuarios[i][2] + ";" + 
                    prontuarios[i][3]
                );
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar prontuários: " + e.getMessage());
        }
    }

    private static void fazerLoginMedico(Scanner scanner) {
        int tentativas = 3;
        boolean loginSucesso = false;
        int indiceMedico = -1;

        while (tentativas > 0 && !loginSucesso) {
            System.out.print("\nLogin: ");
            String login = scanner.next();
            System.out.print("Senha: ");
            String senha = scanner.next();

            for (int i = 0; i < numMedicos; i++) {
                if (loginsMedicos[i].equals(login) && senhasMedicos[i].equals(senha)) {
                    loginSucesso = true;
                    indiceMedico = i;
                    mostrarMenuMedico(scanner, indiceMedico);
                    break;
                }
            }

            if (!loginSucesso) {
                tentativas--;
                System.out.println("Credenciais inválidas! Tentativas restantes: " + tentativas);
            }
        }

        if (!loginSucesso) {
            System.out.println("Acesso bloqueado. Contate o administrador.");
        }
    }

    private static void mostrarMenuMedico(Scanner scanner, int indiceMedico) {
        boolean voltar = false;
        String loginMedico = loginsMedicos[indiceMedico];

        while (!voltar) {
            System.out.println("\n--- MENU MÉDICO (" + loginMedico + ") ---");
            System.out.println("1. Ver prontuário");
            System.out.println("2. Editar prontuário");
            System.out.println("3. Remarcar consulta");
            System.out.println("4. Alterar Minha Senha");
            System.out.println("5. Voltar");
            System.out.print("Escolha: ");
            int opcao = scanner.nextInt();

            switch (opcao) {
                case 1 -> verProntuario(scanner);
                case 2 -> editarProntuario(scanner);
                case 3 -> {
                    System.out.println("\n--- REMARCAÇÃO DE CONSULTA ---");
                    System.out.print("Digite o ID da consulta: ");
                    String idConsulta = scanner.next();
                    System.out.println("Consulta " + idConsulta + " remarcada com sucesso!");
                }
                case 4 -> alterarSenhaMedico(scanner, indiceMedico);
                case 5 -> voltar = true;
                default -> System.out.println("Opção inválida!");
            }
        }
    }

    private static void verProntuario(Scanner scanner) {
        System.out.print("\nDigite o login do paciente: ");
        String idPaciente = scanner.next();
        
        System.out.println("\n--- PRONTUÁRIO DO PACIENTE " + idPaciente + " ---");
        boolean encontrado = false;
        
        for (int i = 0; i < numProntuarios; i++) {
            if (prontuarios[i][0].equals(idPaciente)) {
                encontrado = true;
                System.out.println("Nome: " + prontuarios[i][1]);
                System.out.println("Data: " + prontuarios[i][2]);
                System.out.println("Sintomas: " + prontuarios[i][3]);
                System.out.println("----------------------------------");
            }
        }
        
        if (!encontrado) {
            System.out.println("Nenhum prontuário encontrado para este paciente.");
        }
    }

    private static void editarProntuario(Scanner scanner) {
        System.out.print("\nDigite o login do paciente: ");
        String idPaciente = scanner.next();
        boolean encontrado = false;
        
        for (int i = 0; i < numProntuarios; i++) {
            if (prontuarios[i][0].equals(idPaciente)) {
                encontrado = true;
                System.out.println("\nEditando prontuário existente:");
                System.out.println("Sintomas atuais: " + prontuarios[i][3]);
                System.out.print("Novos sintomas: ");
                scanner.nextLine(); // Limpar buffer
                prontuarios[i][3] = scanner.nextLine();
                
                salvarProntuarios();
                System.out.println("Prontuário atualizado com sucesso!");
                break;
            }
        }
        
        if (!encontrado) {
            System.out.println("Prontuário não encontrado.");
        }
    }

    private static void alterarSenhaMedico(Scanner scanner, int indice) {
        System.out.print("Nova senha: ");
        String novaSenha = scanner.next();
        senhasMedicos[indice] = novaSenha;
        salvarMedicos();
        System.out.println("Senha alterada com sucesso!");
    }

    private static void fazerLoginPaciente(Scanner scanner) {
        int tentativas = 3;
        boolean loginSucesso = false;
        
        
        while (tentativas > 0 && !loginSucesso) {
            System.out.print("\nLogin do Paciente: ");
            String login = scanner.next();
            System.out.print("Senha: ");
            String senha = scanner.next();

            for (int i = 0; i < numPacientes; i++) {
                if (loginsPacientes[i].equals(login) && senhasPacientes[i].equals(senha)) {
                    loginSucesso = true;
                    mostrarMenuPaciente(scanner, login);
                    break;
                }
            }

            if (!loginSucesso) {
                tentativas--;
                System.out.println("Credenciais inválidas! Tentativas restantes: " + tentativas);
            }
        }

        if (!loginSucesso) {
            System.out.println("Acesso bloqueado. Contate o administrador.");
        }
    }

    private static void mostrarMenuPaciente(Scanner scanner, String loginPaciente) {
        boolean voltar = false;

        while (!voltar) {
            System.out.println("\n--- MENU PACIENTE (" + loginPaciente + ") ---");
            System.out.println("1. Responder questionário de saúde mental");
            System.out.println("2. Ver datas disponíveis para consulta");
            System.out.println("3. Marcar consulta");
            System.out.println("4. Alterar minha senha");
            System.out.println("5. Ver meus prontuários");
            System.out.println("6. Criar novo prontuário");
            System.out.println("7. Voltar");
            System.out.print("Escolha: ");
            int opcao = scanner.nextInt();

            switch (opcao) {
                case 1 -> aplicarQuestionario(scanner);
                case 2 -> mostrarDatasDisponiveis();
                case 3 -> agendarConsulta(scanner);
                case 4 -> alterarSenhaPaciente(scanner, loginPaciente);
                case 5 -> verMeuProntuario(loginPaciente);
                case 6 -> criarProntuarioPaciente(scanner, loginPaciente);
                case 7 -> voltar = true;
                default -> System.out.println("Opção inválida!");
            }
        }
    }

    private static void criarProntuarioPaciente(Scanner scanner, String idPaciente) {
        if (numProntuarios >= MAX_PRONTUARIOS) {
            System.out.println("Limite máximo de prontuários atingido!");
            return;
        }
        
        System.out.println("\n--- CRIAR NOVO PRONTUÁRIO ---");
        
        System.out.print("Seu nome completo: ");
        scanner.nextLine(); // Limpar buffer
        String nome = scanner.nextLine();
        
        System.out.print("Data de hoje (DD/MM/AAAA): ");
        String data = scanner.next();
        
        System.out.print("Descreva o que está sentindo: ");
        scanner.nextLine(); // Limpar buffer
        String sintomas = scanner.nextLine();
        
        prontuarios[numProntuarios][0] = idPaciente;
        prontuarios[numProntuarios][1] = nome;
        prontuarios[numProntuarios][2] = data;
        prontuarios[numProntuarios][3] = sintomas;
        numProntuarios++;
        
        salvarProntuarios();
        System.out.println("\nProntuário criado com sucesso!");
        System.out.println("Obrigado por compartilhar suas informações.");
    }

    private static void verMeuProntuario(String idPaciente) {
        System.out.println("\n--- MEU PRONTUÁRIO ---");
        boolean encontrado = false;
        
        for (int i = 0; i < numProntuarios; i++) {
            if (prontuarios[i][0].equals(idPaciente)) {
                encontrado = true;
                System.out.println("Data: " + prontuarios[i][2]);
                System.out.println("Sintomas: " + prontuarios[i][3]);
                System.out.println("----------------------------------");
            }
        }
        
        if (!encontrado) {
            System.out.println("Nenhum prontuário encontrado.");
            System.out.println("Você pode criar um novo prontuário no menu principal.");
        }
    }

    private static void alterarSenhaPaciente(Scanner scanner, String loginPaciente) {
        int indice = -1;
        for (int i = 0; i < numPacientes; i++) {
            if (loginsPacientes[i].equals(loginPaciente)) {
                indice = i;
                break;
            }
        }
        
        if (indice == -1) {
            System.out.println("Erro: Paciente não encontrado!");
            return;
        }
        
        System.out.print("Senha atual: ");
        String senhaAtual = scanner.next();
        
        if (!senhasPacientes[indice].equals(senhaAtual)) {
            System.out.println("Senha atual incorreta!");
            return;
        }
        
        System.out.print("Nova senha: ");
        String novaSenha = scanner.next();
        
        System.out.print("Confirme a nova senha: ");
        String confirmacao = scanner.next();
        
        if (!novaSenha.equals(confirmacao)) {
            System.out.println("As senhas não coincidem!");
            return;
        }
        
        senhasPacientes[indice] = novaSenha;
        salvarPacientes();
        System.out.println("Senha alterada com sucesso!");
    }

    private static void fazerLoginAdministrador(Scanner scanner) {
        int tentativas = 3;
        boolean loginSucesso = false;

        while (tentativas > 0 && !loginSucesso) {
            System.out.print("\nLogin do Administrador: ");
            String login = scanner.next();
            System.out.print("Senha: ");
            String senha = scanner.next();

            if (login.equals(ADMIN_LOGIN) && senha.equals(ADMIN_SENHA)) {
                loginSucesso = true;
                mostrarMenuAdministrador(scanner);
            } else {
                tentativas--;
                System.out.println("Credenciais inválidas! Tentativas restantes: " + tentativas);
            }
        }

        if (!loginSucesso) {
            System.out.println("Acesso bloqueado.");
        }
    }

    private static void mostrarMenuAdministrador(Scanner scanner) {
        boolean voltar = false;

        while (!voltar) {
            System.out.println("\n--- MENU ADMINISTRADOR ---");
            System.out.println("1. Criar Login Médico");
            System.out.println("2. Excluir Login Médico");
            System.out.println("3. Ver todos os prontuários");
            System.out.println("4. Ver todos os pacientes cadastrados");
            System.out.println("5. Voltar");
            System.out.print("Escolha: ");
            int opcao = scanner.nextInt();

            switch (opcao) {
                case 1 -> criarLoginMedico(scanner);
                case 2 -> excluirLoginMedico(scanner);
                case 3 -> verTodosProntuarios();
                case 4 -> verTodosPacientes();
                case 5 -> voltar = true;
                default -> System.out.println("Opção inválida!");
            }
        }
    }

    private static void verTodosProntuarios() {
        System.out.println("\n--- TODOS OS PRONTUÁRIOS ---");
        for (int i = 0; i < numProntuarios; i++) {
            System.out.println("Paciente: " + prontuarios[i][1] + " (" + prontuarios[i][0] + ")");
            System.out.println("Data: " + prontuarios[i][2]);
            System.out.println("Sintomas: " + prontuarios[i][3]);
            System.out.println("----------------------------------");
        }
        System.out.println("Total de prontuários: " + numProntuarios);
    }
    
    private static void verTodosPacientes() {
        System.out.println("\n--- PACIENTES CADASTRADOS ---");
        for (int i = 0; i < numPacientes; i++) {
            System.out.println((i+1) + ". " + loginsPacientes[i]);
        }
        System.out.println("Total de pacientes: " + numPacientes);
    }

    private static void criarLoginMedico(Scanner scanner) {
        if (numMedicos >= MAX_MEDICOS) {
            System.out.println("Limite máximo de médicos atingido!");
            return;
        }
        
        System.out.print("Novo login para médico: ");
        String novoLogin = scanner.next();
        System.out.print("Nova senha: ");
        String novaSenha = scanner.next();

        for (int i = 0; i < numMedicos; i++) {
            if (loginsMedicos[i].equals(novoLogin)) {
                System.out.println("Login já existe!");
                return;
            }
        }

        loginsMedicos[numMedicos] = novoLogin;
        senhasMedicos[numMedicos] = novaSenha;
        numMedicos++;
        salvarMedicos();
        System.out.println("Login médico criado com sucesso!");
    }

    private static void excluirLoginMedico(Scanner scanner) {
        System.out.print("Login do médico a excluir: ");
        String loginExcluir = scanner.next();

        int index = -1;
        for (int i = 0; i < numMedicos; i++) {
            if (loginsMedicos[i].equals(loginExcluir)) {
                index = i;
                break;
            }
        }
        
        if (index == -1) {
            System.out.println("Login não encontrado.");
            return;
        }

        for (int i = index; i < numMedicos - 1; i++) {
            loginsMedicos[i] = loginsMedicos[i + 1];
            senhasMedicos[i] = senhasMedicos[i + 1];
        }
        
        numMedicos--;
        loginsMedicos[numMedicos] = null;
        senhasMedicos[numMedicos] = null;
        salvarMedicos();
        System.out.println("Login médico excluído com sucesso!");
    }

    private static void aplicarQuestionario(Scanner scanner) {
        int pontuacaoTotal = 0;

        System.out.println("\n--- QUESTIONÁRIO DE SAÚDE MENTAL ---");
        System.out.println("Responda com valores de 0 a 5:");
        System.out.println("(0 = Nunca/Quase nunca | 5 = Sempre/Quase sempre)");

        System.out.print("\n1. Quão cansado você está? ");
        pontuacaoTotal += validarResposta(scanner.nextInt());

        System.out.print("2. Com que frequência você se sente estressado? ");
        pontuacaoTotal += validarResposta(scanner.nextInt());

        System.out.print("3. Quão sozinho ou triste você se sente? ");
        pontuacaoTotal += validarResposta(scanner.nextInt());

        System.out.print("4. Nível de tristeza ou desânimo? ");
        pontuacaoTotal += validarResposta(scanner.nextInt());

        System.out.print("5. Sua rotina é monótona? ");
        pontuacaoTotal += validarResposta(scanner.nextInt());

        System.out.print("6. Você se sente deixado de lado ou ignorado? ");
        pontuacaoTotal += validarResposta(scanner.nextInt());

        System.out.println("\n--- RESULTADO ---");
        System.out.println("Pontuação total: " + pontuacaoTotal + "/30");

        if (pontuacaoTotal <= 10) {
            System.out.println("Sua saúde mental está excelente!");
        } else if (pontuacaoTotal <= 15) {
            System.out.println("Sua saúde mental está boa, mas fique atento!");
        } else if (pontuacaoTotal <= 20) {
            System.out.println("Sua saúde mental está desgastada - Recomendamos atenção");
        } else if (pontuacaoTotal <= 25) {
            System.out.println("Sua saúde mental está comprometida - Procure um profissional");
        } else {
            System.out.println("ATENÇÃO: Sua saúde mental está em estado crítico!");
            System.out.println("\n--- CONTATOS DE EMERGÊNCIA ---");
            for (String contato : CONTATOS_EMERGENCIA) {
                System.out.println(contato);
            }
        }
    }

    private static int validarResposta(int resposta) {
        return Math.max(0, Math.min(5, resposta));
    }

    private static void mostrarDatasDisponiveis() {
        System.out.println("\n--- DATAS DISPONÍVEIS ---");
        System.out.println("Período: 29/05/2025 até 22/12/2025");
        System.out.println("Horários: 8:00 às 12:00 e 14:00 às 18:00");

        System.out.println("\nPróximas 10 datas disponíveis:");
        for (String data : DATAS_DISPONIVEIS) {
            System.out.println("- " + data);
        }
    }

    private static void agendarConsulta(Scanner scanner) {
        System.out.println("\n--- AGENDAMENTO DE CONSULTA ---");
        mostrarDatasDisponiveis();

        System.out.print("\nDigite a data desejada (ex: 29/05/2025): ");
        String data = scanner.next();

        System.out.println("Horários disponíveis para " + data + ":");
        for (String horario : HORARIOS_DISPONIVEIS) {
            System.out.print(horario + " ");
        }

        System.out.print("\nDigite o horário desejado (ex: 8:00): ");
        String horario = scanner.next();

        System.out.println("\nConsulta agendada com sucesso!");
        System.out.println("Data: " + data);
        System.out.println("Horário: " + horario);
        System.out.println("Compareça 10 minutos antes do horário marcado.");
    }
}