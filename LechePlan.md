> Note: Code in a way that makes testing easier!!!
### Project Plan
- Plan
    - Plan UI flow
    - Plan data flow (decide what my algorithm does and what they're for and what collection to use)
- Work
    - Code GUI (core functionalities)
    - Code initial CRUD system
    - Make mock data
    - Code Backend


FRONT
- GUI? later na yannnn

BACK
- Queue (FIFO)
	- managing waiting lines
- Priority Queue
	- Prioritizing Urgent cases
- LinkedList
	- Registers patients

### UI plan
#### Initial UI plan
1. **Nurse/staff interface (required):** register patient, set priority or flag an emergency, update status, call next, skip a no-show, reassign to a doctor or room.
2. **Public display (optional but cheap):** read-only, queue numbers only.

#### UI
*First thing*
1. Log in for Nurse
	- NurseID
	- Password

Then two windows appear*

*Staff side*
1. Queue handling
	- Register patients
	- Sets priority and sets appointment

*Patient Side* (Second window, for a second monitor)
1. Request appointments (view)
	- Plan time and date
	- See when a doctor is avialable
2. View Queue
	- Patient views the ff:
		- Queue no.
		- Doctor
		- Room

#### Major notes
- Staff can registers patients 

### Project
- GUI with Event handling
- Data processing, includes Searching, Sorting, and Storing of data
	- Utilize Search algorithm to find patients and unoccupied rooms
	- Utilize Sorting algorithm to organize the data before storing
	- I'll probably need to have it reorganize every time due to priority patients
	- Create a separate class to handle the multithreading and perhaps handle there the search, sorting algo, and maybe even the main
- File Writer Class?? (Not sure, might need for JDBC)
- Database mock data 

- Iterative Quick Sort
- Binary Search

------

## Queueing Systems
It's a process involving registration, prioritization, consultation, and sometimes multiple departments.

Note that the system may have multiple doctors in the clinic and as such we'll need to monitor which is available.

> Might implement a layout of the doctors

----

##### Resources
- [com.mindfusion.scheduling.MonthRangeSettings Members | Java Swing Scheduler Library | Documentation](https://mindfusion.dev/docs/java-swing/scheduling/T_com_mindfusion_scheduling_MonthRangeSettings_Members.htm)
