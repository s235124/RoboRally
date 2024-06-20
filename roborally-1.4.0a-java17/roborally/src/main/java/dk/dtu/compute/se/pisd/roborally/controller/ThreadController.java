package dk.dtu.compute.se.pisd.roborally.controller;

public class ThreadController implements Runnable {

    private volatile boolean flag = true;

    int id;

    public ThreadController(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        while (flag) {
            // Polling
            boolean result = pollServer();

            System.out.println(result);

            if (result) {
                flag = false;
            }

            try { // I sleep
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // Handle the interruption appropriately
            }
        }
    }

    private boolean pollServer() {
        try {
            return HttpController.isReady(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stopPolling() {
        flag = false;
    }
}
