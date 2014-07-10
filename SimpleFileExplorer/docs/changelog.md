# 2014.7.10 #
##### Fix Bug:
  &nbsp;&nbsp;&nbsp;&nbsp;When users open directory contains empty image files, the app activity will crash.
  
##### Reason:
  &nbsp;&nbsp;&nbsp;&nbsp;The function that generate bitmap use *LruCache.put* function.when the bitmap is  
  null, the "LruCache.put" function throw exception.  
  
##### Solved:
  &nbsp;&nbsp;&nbsp;&nbsp;add a *if* judgment when bitmap is null, return null to skip the *LruCache.put* function.  
  And modify the bitmap setting action when null value return.  
  
##### Feature add:
  &nbsp;&nbsp;&nbsp;&nbsp;When rename files, the textview don't show the original name.  
