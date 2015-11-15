Radio Boxes
---

Radio boxes allow users to select a single item from a list of predetermined values.

To create a radio box list:

```
	TerminalSize size = new TerminalSize(14, 10);
	RadioBoxList<String> radioBoxList = new RadioBoxList<String>(size);
```
To add radioboxes to a list:

```
	radioBoxList.addItem("item 1");
	radioBoxList.addItem("item 2");
	radioBoxList.addItem("item 3");
```

To get the currently checked item:

```
	String checkedItems = radioBoxList.getCheckedItem();
```

### Screenshot

![](screenshots/radio_boxes.png)

