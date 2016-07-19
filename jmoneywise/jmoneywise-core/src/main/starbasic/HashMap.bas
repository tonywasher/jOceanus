'*******************************************************************************
'* jMoneyWise: Finance Application
'* Copyright 2012,2016 Tony Washer
'*
'* Licensed under the Apache License, Version 2.0 (the "License");
'* you may not use this file except in compliance with the License.
'* You may obtain a copy of the License at
'*
'*   http://www.apache.org/licenses/LICENSE-2.0
'*
'* Unless required by applicable law or agreed to in writing, software
'* distributed under the License is distributed on an "AS IS" BASIS,
'* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
'* See the License for the specific language governing permissions and
'* limitations under the License.
'* ------------------------------------------------------------
'* SubVersion Revision Information:
'* $URL$
'* $Revision$
'* $Author$
'* $Date$
'******************************************************************************/
'Number of hash nodes
Private Const defaultHashNodes As Integer = 47

'HashMap entry
Private Type hashEntry
	hashValue As Long
	strKey As String
	objValue As Object
	nextEntry As Object
	
	idx1 As Integer
	idx2 As Integer
	idx3 As Integer
End Type

'HashMap 
Private Type hashMap
	numValues As Integer
	numNodes As Integer
	hashList() As Object
		
	idx1 As Integer
	idx2 As Integer
	idx3 As Integer
End Type

'HashIterator
Public Type hashIteratorCtl
	objMap As hashMap
	idxElement As Integer	
	nextEntry As Object
	
	idx1 As Integer
	idx2 As Integer
	idx3 As Integer
End Type

'Allocate the map
Public Function allocateHashMap() As hashMap
	Dim myMap As New hashMap
	Dim myArray(defaultHashNodes-1) As Object
	myMap.numValues = 0
	myMap.numNodes = defaultHashNodes
	myMap.hashList = myArray 
	allocateHashMap = myMap
End Function

'Reset hash map
Public Sub resetHashMap(ByRef map As hashMap)
	Dim myArray(defaultHashNodes-1) As Object
	map.numValues = 0
	map.numNodes = defaultHashNodes
	map.hashList = myArray 
End Sub

'Calculate hash of entry
Private Function calculateHash(ByRef key As String) As Long
	Dim hash As Long
	
	'Initialise hash as length
	hash = 0
	keyLen = Len(key)
	
	' Loop through the characters 
	For i=1 To keyLen
		'Access character and build into hash
		myChar = Mid(key, i, 1)
		hash = hash + Asc(myChar)
	Next i
	
	'Return the hash
	calculateHash = hash
End Function

'Locate entry
Public Function findHashKey(ByRef map As hashMap, _
							ByRef key As String) As Object	
	'Calculate the hash
	hash = calculateHash(key)

	'Calculate the index to use 
	index = hash MOD map.numNodes
	
	'Access the entry
	Dim baseEntry As Object
	Set baseEntry = map.hashList(index)
	Set curEntry = baseEntry
	
	'Loop while there are entries
	While Not isNull(curEntry)
			
		' If we have matched the hash and key
		If (curEntry.hashValue = hash) And (curEntry.strKey = key) Then
			'Set value and return 
			Set findHashKey = curEntry.objValue
			Exit Function
		End If
		
		'Move to next entry 
		Set curEntry = curEntry.nextEntry
	Wend
	
	'Set not found
	Set findHashKey = Nothing
End Function

'Locate entry
Public Sub putHashKey(ByRef map As hashMap, _
				  	  ByRef key As String, _
					  ByRef value As Object)
	'Calculate the hash
	hash = calculateHash(key)

	'Calculate the index to use 
	index = hash MOD map.numNodes
	
	'Access the entry
	Dim baseEntry As Object
	Set baseEntry = map.hashList(index)
	Set curEntry = baseEntry
	
	'Loop while there are entries
	While Not isNull(curEntry)
		' If we have matched the hash and key
		If (curEntry.hashValue = hash) And (curEntry.strKey = key) Then
			'Set new value 
			Set curEntry.objValue = value
			Exit Sub
		End If
		
		'Move to next entry 
		Set curEntry = curEntry.nextEntry
	Wend
		
	'Access new entry
	Dim newEntry As New hashEntry
	
	'Store values
	newEntry.hashValue = hash
	newEntry.strKey = key
	Set newEntry.objValue = value
	Set newEntry.nextEntry = baseEntry
	
	'Add to list 
	Set map.hashList(index) = newEntry
End Sub

'Allocate an iterator
Public Function hashIterator(ByRef map As hashMap) As hashIteratorCtl
	Dim myIterator As New hashIteratorCtl
	Set myIterator.objMap = map
	myIterator.idxElement = -1
	Set hashIterator = myIterator 
End Function

'Is there another element?
Public Function hashHasNext(ByRef itr As hashIteratorCtl) As Boolean
	'Access map and location
	Dim hashEnt As Object
	Set myMap = itr.objMap
	idxEl = itr.idxElement
	Set hashEnt = itr.nextEntry
	
	'If we have a next entry
	If Not isNull(hashEnt) Then
		'Return true
		hashHasNext = True()
		Exit Function
	End If 
	
	'Increment element 
	idxEl = idxEl + 1
		
	'Loop to find a valid entry
	While (idxEl < myMap.numNodes) 
		'Access element
		Set hashEnt = myMap.hashList(idxEl)
		
		'If we have an entry 
		If Not isNull(hashEnt) Then
			'Return indication		
			hashHasNext = True()
			Exit Function
		End If
		
		'Move to next element
		idxEl = idxEl + 1
	Wend
	
	'Set result
	hashHasNext = False()
End Function

'Obtain next element
Public Function hashNext(ByRef itr As hashIteratorCtl) As Variant
	'Access map and location
	Dim hashEnt As Object
	Set myMap = itr.objMap
	idxEl = itr.idxElement
	Set hashEnt = itr.nextEntry
	
	'If we do not have a next entry
	If isNull(hashEnt) Then
		'Increment element 
		idxEl = idxEl + 1
		
		'Loop to find a valid entry
		Do While (idxEl < myMap.numNodes) 
			'Access element
			Set hashEnt = myMap.hashList(idxEl)
		
			'If we have an entry 
			If Not isNull(hashEnt) Then
				Exit Do
			End If
		
			'Move to next element
			idxEl = idxEl + 1
		Loop
	End If
		
	'Return entry
	Set hashNext = hashEnt.objValue
	itr.idxElement = idxEl
	Set itr.nextEntry = hashEnt.nextEntry
End Function
