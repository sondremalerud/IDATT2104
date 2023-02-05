# include <functional>
# include <iostream>
# include <list>
# include <mutex>
# include <condition_variable>
# include <thread>
# include <vector>

// For sleep function to work - Linux
// #include <unistd.h>

// For sleep function to work - Windows
#include <windows.h>

using namespace std;

class Workers {
    list<function<void()>> tasks; // List with tasks that are yet to be completed
    vector<thread> worker_threads; // List with threads in use

    int amount_of_threads = 0;
    mutex tasks_mutex;
    condition_variable cv;
    bool running = true;
    bool wait = true;

    public:
        Workers(int amount_of_threads) {
            this->amount_of_threads = amount_of_threads;
        }

    // Creates a specified amount of threads
    void start() {
        worker_threads = vector<thread>();
        
        for (int i = 0; i < amount_of_threads; i++) {
            worker_threads.emplace_back([this] {
                while (running) {
                    function<void()> task;
                    {
                        unique_lock<mutex> lock(tasks_mutex);
                        while (wait){
                            cv.wait(lock); // Unlocks mutex and waits until notified, which happens after mutex is locked again
                        }
                        
                        if (!tasks.empty()) {
                            task = *tasks.begin(); // Copy task for later use
                            tasks.pop_front(); // Remove task from list
                        }
                    }
                    if (task) task(); // Run task outside of mutex lock if "task" points to anything       
                }
            });

            // Thread completed a task, wake up next thread waiting
            {
                unique_lock<mutex> lock(tasks_mutex);
                wait = false;
                cv.notify_one();
            }
        }
    }

    // Adds a task to the tasks list
    void post(const function<void()> &task) {
        unique_lock<mutex> lock(tasks_mutex); // Makes sure the list gets updated without a thread trying to access/change it
        tasks.emplace_back(task);
        wait = false;
        cv.notify_one(); // Wakes one thread waiting for a task
    }



    // Skal stoppe alle trådene når f.eks tasklisten er tom
    void stop() {
        while (running) {
            if (tasks.empty()) {
                cout << "Running set to false" << endl;
                running = false;
                cv.notify_all();
            }
        }
    }

    // Posts the task after a given amount of miliseconds
    void post_timeout(const function<void()> &task, int miliseconds) {
        Sleep(miliseconds);
        unique_lock<mutex> lock(tasks_mutex);
        tasks.emplace_back(task);
    }

    void join() {
        for (thread &t : worker_threads) {
            t.join();
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
    });


    worker_threads.post([] {
        cout << "Task B, might run in parallel with A" << endl;
    });

    event_loop.post([] {
        cout << "Task C, might run in parallel with A and B" << endl;
    });
    event_loop.post([] {
        cout << "Task D, will run after C, might run in parallel with A and B" << endl;
    });

    // 2second delayed post
    worker_threads.post_timeout([] {
    cout << "2000ms delayed task" << endl;
    }, 2000);

    // random testing
    worker_threads.post([] {
        cout << "Task 123" << endl;
    });

    worker_threads.post([] {
        cout << "Task 123" << endl;
    });
    worker_threads.post([] {
        cout << "Task 123" << endl;
    });
    worker_threads.post([] {
        cout << "Task 123" << endl;
    });
    worker_threads.post([] {
        cout << "Task 123" << endl;
    });

    worker_threads.stop();
    event_loop.stop();

    worker_threads.join(); // Calls join() on the worker threads
    event_loop.join(); // Calls join() on the event thread

    cout << "Program finished" << endl;
    
    return 0;
}
