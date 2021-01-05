## High
- [ ] Support for new year
- [ ] Scheduled recurring expense
- [ ] Backup on app startup if auto backup hasn't happened in a while
- [ ] Fix LiveData access for most VM/Repos (Look at Recurring implementation for correct way)
- [x] Test backup with value for income column but not flag column to see if empty cell is transmitted
- [x] Write recurring expenses to Sheet
- [x] Write add type to Sheet
- [x] Annual expense UI
- [x] Fix calculation for monthly and annual
- [x] Thoroughly test annual features
- [x] Test syncing older expenses into the app (due to change to reading/writing all strings)
- [x] Update all expenses to include identifier
- [x] Make backup worker upload all expenses again
- [x] Update Filter for monthly and annual expenses
- [x] Fix CategoryRepository.setCategoris
- [x] Make annual categories work
- [x] Make annual budget work
- [x] SQL Builder

## Medium
- [ ] Tap on budget/payment/category to view filtered expenses
- [ ] Restore access
- [ ] Save time zone in backup
- [ ] Synchronized access to repo, apphelper
- [ ] Backup to file

## Low
- [ ] Remove limit on number of budgets, categories and payment methods
- [ ] Save expense on the fly and make it editable on the next run (in case app is killed, information shouldn't be lost)
- [ ] Widget with total used
- [ ] Fix fragment refreshing
- [ ] Error messages
- [ ] Upgrade Firebase/Crashlytics
