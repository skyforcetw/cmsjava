=流程說明=

將SpectraWin設定成Auto Modes,設定方法為
1.進入Setup>Auto Modes>
2.New Measurement>Overwrite Current Measurement
3.Continuous Measurement 打勾並且設定要測量的次數
4.Auto Save 打勾並且設定檔案儲存位置
5.依需求設定PatchShower的 interval/file size threshold/多重取樣
6.設定PatchShower的色塊檔
7.設定PatchShower的監控檔案
8.按PatchShower的 "開始"
9.按SpectraWin的 "Measure" 開始測量

ps:由於監控檔案可能失敗,造成資料複製出錯,PatchShower此時會重複show出色塊,以便得到正確的資料.
但是此舉動會造成測量色塊數不足,需要在事後再補足這些測量失敗的色塊.
在測量完畢後,PatchShower會把失敗的數量顯示在Title.

=介面說明=

視窗大小 :
設定程式視窗所佔的大小

設定>interval :
監察檔案狀態的時間間隔.
間隔越密集,越能嚴密監控檔案狀態,但是太過密集可能造成錯誤,太過鬆散可能導致監控不足.

設定>file size threshold :
檔案大小作動的門檻值.
由於程式監控檔案時,SpectraWin可能正好寫入檔案到一半,此時將檔案複製有可能會造成失敗,因此限制一定大小才對檔案作複製動作,是為了確保SpectraWin已經把檔案寫入完畢,這個值需要經過實驗才能決定.

多重取樣 :
為了應付雜訊,因此可以設定對單一色塊進行多次取樣,再由程式進行平均,減低雜訊的影響

色塊檔 :
將欲顯示的色塊檔指定好,格式為GretagMacbeth ASCII File格式

監控檔案 :
指定要監控的SpectraWin產生的測試結果檔