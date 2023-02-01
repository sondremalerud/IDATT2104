#include <functional>
# include <iostream>
# include <list>
# include <mutex>
# include <thread>
# include <vector>
using namespace std;


class Workers {
    list<function<void()>> tasks; // Lista med tasks som trådene skal fullføre
    int amount_of_threads = 0;
    mutex tasks_mutex;
    condition_variable cv;
    vector<thread> worker_threads;
    bool running = true;

    public:
        Workers(int amount_of_threads) {
            this->amount_of_threads = amount_of_threads;
        }

    // Creates a specified amount of threads
    void start() {
        worker_threads = vector<thread>(amount_of_threads);
        
        for (int i = 0; i < amount_of_threads; i++) {
            worker_threads.emplace_back([this] {
                while (running) {
                    //todo
                }
    

            });

        }

    }

    // Legger til en funksjon i tasks-lista
    void post(function<void()> task) {
        unique_lock<mutex> lock(tasks_mutex);
        tasks.emplace_back(task)
        cv.notify_one(); // Vekker én tråd som venter på å utføre en task
    }



    
    void stop() {

    }

    void post_timeout() {
        
    }

    void join() {
        for (auto &thread : worker_threads) {
            thread.join();
        }
    }

};







int main() {
    Workers worker_threads(4);
    Workers event_loop(1);

    worker_threads.start();
    event_loop.start();

    worker_threads.post([] {
        // Task A
    });
    worker_threads.post([] {
        // Task B
        // might run in parallel with task A
    });

    event_loop.post([] {
        // Task C
        // Might run in parallel with task A and B
    });
    event_loop.post([] {
        // Task D
        // Will run after task C
        // Might run in parallel with task A and B
    });

    worker_threads.join(); // Calls join() on the worker threads
    event_loop.join(); // Calls join() on the event thread
}
