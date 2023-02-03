#include <functional>
# include <iostream>
# include <list>
# include <mutex>
# include <condition_variable>
# include <thread>
# include <vector>
using namespace std;


class Workers {
    list<function<void()>> tasks; // Lista med tasks som trådene skal fullføre
    vector<thread> worker_threads;

    int amount_of_threads = 0;
    mutex tasks_mutex;
    condition_variable cv;
    bool running, wait = true;

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
                    function<void()> task;
                    {
                        unique_lock<mutex> lock(tasks_mutex);
                        while (wait && running){
                            cv.wait(lock); // Locks thread until it is notified, which happens when post() is used
                        }
                        
                        if (!tasks.empty()) {
                            task = *tasks.begin(); // Copy task for later use
                            tasks.pop_front(); // Remove task from list
                        }
                    }
                    if (task) task(); // Run task outside of mutex lock                  
                }
            });

            {
                unique_lock<mutex> lock(tasks_mutex);
                wait = false;
                cv.notify_one();
            }
        }
    }

    // Legger til en funksjon i tasks-lista
    void post(function<void()> &task) {
        unique_lock<mutex> lock(tasks_mutex);
        tasks.emplace_back(task);
        cv.notify_one(); // Vekker én tråd som venter på å utføre en task
    }



    // Skal stoppe alle trådene når f.eks tasklisten er tom
    void stop() {
        while (running) {
            if (tasks.empty()) {
                running = false;
            }
        }
    }

    // Kjører task argumentet etter et gitt antall milisekund
    void post_timeout(int miliseconds) {
        
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
        cout << "Task A" << endl;
        // Task A
    });
    worker_threads.post([] {
        cout << "Task B, might run in parallel with A" << endl;
        // Task B
        // might run in parallel with task A
    });

    event_loop.post([] {
        cout << "Task C, might run in parallell with A and B" << endl;
        // Task C
        // Might run in parallel with task A and B
    });
    event_loop.post([] {
        cout << "Task D, will run after C, might run in parallel with A and B" << endl;
        // Task D
        // Will run after task C
        // Might run in parallel with task A and B
    });


    worker_threads.join(); // Calls join() on the worker threads
    event_loop.join(); // Calls join() on the event thread

    worker_threads.stop();
    event_loop.stop();
    
    return 0;
}
