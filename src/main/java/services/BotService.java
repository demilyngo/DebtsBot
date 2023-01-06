package services;

import Entities.DebtEntity;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Locale;

public class BotService extends TelegramLongPollingBot {

    private int sum = 0;
    private String receiver = null;
    private String debtor = null;
    private static int operation = -1;

    @Override
    public String getBotUsername() {
        return "Telegram Bot";
    }


    @Override
    public String getBotToken() {
        return "5733472368:AAFbAzc3PG34RqrS0Ipvr2LqAZ-EQHpZYRk";
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
        message.setChatId(update.getMessage().getChatId().toString());
        if(update.getMessage().getText().toLowerCase(Locale.ROOT).equals("/cancel")) {
            sum = 0;
            debtor = null;
            receiver = null;
            operation = -1;
            return;
        }
        if(update.getMessage().getText().toLowerCase(Locale.ROOT).equals("/help") ||
                update.getMessage().getText().toLowerCase(Locale.ROOT).equals("/bot") ||
                update.getMessage().getText().toLowerCase(Locale.ROOT).equals("/debt")) {
            sendMessage(message, """
                    Справка:
                    /add - добавить долг
                    /sub - списать определенную сумму долга
                    /paioff - полностью списать долг
                    /cancel - выход
                    /checkdebtor
                    /checkreceiver
                    /all - показать все долги""");
            return;
        }
        if (operation == -1) {
            if (update.getMessage().getText().toLowerCase(Locale.ROOT).equals("/add")) {
                operation = 1;
                sendMessage(message, "Сколько? ");
            }
            if (update.getMessage().getText().toLowerCase(Locale.ROOT).equals("/sub")) {
                operation = 2;
                sendMessage(message, "Сколько? ");
            }
            if (update.getMessage().getText().toLowerCase(Locale.ROOT).equals("/paioff")) {
                operation = 3;
                sendMessage(message, "Введите имя должника.");
            }
            if (update.getMessage().getText().toLowerCase(Locale.ROOT).equals("/checkdebtor")) {
                operation = 4;
                sendMessage(message, "Введите имя должника.");
            }
            if (update.getMessage().getText().toLowerCase(Locale.ROOT).equals("/checkreceiver")) {
                operation = 5;
                sendMessage(message, "Введите имя ждуна.");
            }
            if(update.getMessage().getText().toLowerCase(Locale.ROOT).equals("/all")){
                operation = -1;
                completeOperation(message, receiver, debtor, sum, operation);
            }
            return;
        }

        if (operation == 4 || operation == 5) {
            if(operation==4?(debtor = update.getMessage().getText()).isBlank():(receiver = update.getMessage().getText()).isBlank()) {
                sendMessage(message, "Нужно имя.");
                return;
            }
            completeOperation(message, receiver, debtor, sum, operation);
            sum = 0;
            debtor = null;
            receiver = null;
            operation = -1;
            return;
        }

        //Getting sum
        if (sum <= 0 && (operation == 1 || operation == 2)) {
            try {
                sum = Integer.parseInt(update.getMessage().getText());
            }
            catch (NumberFormatException e) {
                sum = 0;
                sendMessage(message, "Нужно число больше 0. ");
                return;
            }
            if (sum <= 0) {
                sum = 0;
                sendMessage(message, "Нужно число больше 0. ");
                return;
            }
            sendMessage(message, "Введите имя должника.");
            return;
        }
        //Getting debtor
        if (debtor == null) {
            if ((debtor = update.getMessage().getText()).isBlank()) {
                sendMessage(message, "Нужно имя.");
                return;
            }
            sendMessage(message, "Кому должен?");
            return;
        }
        //Getting receiver
        if (receiver == null) {
            if ((receiver = update.getMessage().getText()).isBlank()) {
                sendMessage(message, "Нужно имя.");
                return;
            }
            completeOperation(message, receiver, debtor, sum, operation);
            sum = 0;
            debtor = null;
            receiver = null;
            operation = -1;
            return;
        }
    }

    public void completeOperation (SendMessage message, String receiver, String debtor, int sum, int operation) {
        DebtService debtService = new DebtService();
        DebtEntity newDebt = new DebtEntity(receiver, debtor, sum);
        //List<DebtEntity> allDebts = debtService.findAllDebts();

        if (operation == -1) {
            for(DebtEntity debt : debtService.findAllDebts()) {
                sendMessage(message, debt.getDebtor() +
                        " должен " + debt.getReceiver() +
                        " " + debt.getSum() + " рублей.");
            }
            return;
        }

        if (operation == 4) {

            for (DebtEntity currentDebt : debtService.findByDebtor(debtor)) {
                if (currentDebt.getDebtor().equals(debtor)) {
                    sendMessage(message, currentDebt.getDebtor() +
                            " должен " + currentDebt.getReceiver() +
                            " " + currentDebt.getSum() + " рублей.");
                }
            }
            return;
        }
        if (operation == 5) {
            for (DebtEntity currentDebt : debtService.findByReceiver(receiver)) {
                if (currentDebt.getReceiver().equals(receiver)) {
                    sendMessage(message, currentDebt.getDebtor() +
                            " должен " + currentDebt.getReceiver() +
                            " " + currentDebt.getSum() + " рублей.");
                }
            }
            return;
        }

        for (DebtEntity currentDebt : debtService.findAllDebts()) {

            // New debt equals existing debt
            if (currentDebt.getReceiver().equals(newDebt.getReceiver()) && currentDebt.getDebtor().equals(newDebt.getDebtor())) {
                if (operation == 1)
                    currentDebt.setSum(newDebt.getSum() + currentDebt.getSum());

                if (operation == 2){
                    currentDebt.setSum(currentDebt.getSum() - newDebt.getSum());
                    checkForCorrectDebt(message, debtService, currentDebt);
                }

                if (operation == 3) {
                    debtService.deleteDebt(currentDebt);
                    sendMessage(message, "Долг польностью выплачен.");
                    return;
                }
                debtService.updateDebt(currentDebt);
                sendMessage(message, "Текущий долг: " + currentDebt.getDebtor() +
                        " должен " + currentDebt.getReceiver() +
                        " " + currentDebt.getSum() + " рублей.");
                return;
            }

            // New debt mirroring existing debt
            if (currentDebt.getReceiver().equals(newDebt.getDebtor()) && currentDebt.getDebtor().equals(newDebt.getReceiver()) && operation == 1) {
                currentDebt.setSum(currentDebt.getSum() - newDebt.getSum());
                checkForCorrectDebt(message, debtService, currentDebt);
                debtService.updateDebt(currentDebt);
                sendMessage(message, "Текущий долг: " + currentDebt.getDebtor() +
                        " должен " + currentDebt.getReceiver() +
                        " " + currentDebt.getSum() + " рублей.");
                return;
            }


        }
        // If trying to write off non-existent debt
        if(operation == 2 || operation == 3) {
            sendMessage(message, "Такого долга нет.");
            return;
        }
        debtService.saveDebt(newDebt);
        sendMessage(message, "Новый долг добавлен: " + newDebt.getDebtor() +
                " должен " + newDebt.getReceiver() +
                " " + newDebt.getSum() + " рублей.");
    }

    public void checkForCorrectDebt (SendMessage message, DebtService debtService, DebtEntity currentDebt) {
        //Debt is paid off
        if(currentDebt.getSum() == 0) {
            debtService.deleteDebt(currentDebt);
            sendMessage(message, "Долг польностью выплачен.");
            return;
        }
        //Debt < 0 means change of receiver and debtor
        if (currentDebt.getSum() < 0) {
            String buf = currentDebt.getReceiver();
            currentDebt.setReceiver(currentDebt.getDebtor());
            currentDebt.setDebtor(buf);
            currentDebt.setSum(Math.abs(currentDebt.getSum()));
        }
    }

    public void sendMessage(SendMessage message, String text) {
        message.setText(text);
        try {
            execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}