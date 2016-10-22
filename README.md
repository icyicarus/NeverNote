# NeverNote Project Proposal

#### by Xuenan Xu & Yicheng Jia

In our daily life, there are trivial things that we need to record and remember. A few years ago, we still prefer written records, but with the development of technology, plain text is outdated due to its __limitation of content and volatile media__. We desire a more convenient way of recording, which is rich in content and of course lasting, to satisfy our daily use. Suppose we go sightseeing, travel photos can better express the magnificent view of mountains and rivers, rather than pale text; consider we are in a very important meeting, it's vivid that a record can explain the purpose of the meeting better than plain text; assume we are observing complex scientific experiments, nothing can demonstrate the experiment processes and results better than a video; in short, notes on paper -- or be more advanced, typed text notes -- are not capable of these common scenes, a multimedia notebook which can record rapidly and conveniently would work better.
As the operating system which shares the largest part of smart phone market, individuals and companies all over the world have developed thousands of applications for Android, among them there are elegant, beautiful and unique notebook applications, but after our brief analyzation, they more or less got some disadvantages, of which we will demonstrate below:

* __Xunfei Voice Note__: Using the API from iFlyTek, it's capable of transfer voice to text rapidly, but unable to add multimedia files to notes, only in text format.
* __Orange Diary__: It's able to add weather and moods to notes, but there is a limitation of 5 pictures and 1 audio in each note, and disability to add videos.
* __Voice Notes__: Capable of record audio, take picture and add comments, and show them in timeline or customize tags to categorize; disability to add video as well, and the operation to add a note is somehow complicated, not for fast noting.
* __Youdao Cloud Notes__: Notes are categorized by their formats, like text, audio, handwriting and picture, but unable to merge these kinds of notes together, that is lack of connections between note elements.
* __FreeNote__: Enhanced ability to merge text, audio and picture (even add audio elements in the middle of a line, like `today is a <audio content> day`, really amazing), but the operation is too complex, not suitable for rapid noting, like record a meeting. Another disadvantage of FreeNote is the disability of categorizing, the whole application is like a paper notebook, categorized by paging.

We drew a chart demonstrating the characteristics of these applications, shown below:
<font size=1>

|              | Xunfei Voice Note | Orange Diary | Voice Notes | Youdao Cloud Note | FreeNote |
|--------------|-------------------|--------------|-------------|-------------------|----------|
| Operation    | Simple            | Simple       | Complex     | Simple            | Simple   |
| Media Type   | Lack              | Lack         | Rich        | Rich              | Rich     |
| Connection   | No                | Yes          | Yes         | Yes but incomplete| Yes      |
| Categorizing | No                | Date         | Date, Tag   | No                | Page     |

</font>
We can tell that none of these applications are capable of video recording, and those rich in functions are complex to operate, and those simple in operating lack of functions. None of them satisfy our "Fast and Complete" principle. It's our purpose, after analyzed typical examples of notebook applications, to __re-design__ and implement a __simple-to-operate__ and __rich-in-function__ notebook application.
