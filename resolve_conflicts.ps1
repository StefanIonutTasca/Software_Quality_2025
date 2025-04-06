# Script to resolve merge conflicts between development and feature/package-creation branches
# Created by Cascade AI

# Define the base directory
$baseDir = "c:\Users\stefa\Desktop\School\Programare\Software_Quality_2025"

# Step 1: Make sure we're on the feature branch
Write-Host "Checking out the feature branch..." -ForegroundColor Cyan
git checkout feature/package-creation

# Step 2: Fetch the latest changes from the remote repository
Write-Host "Fetching the latest changes..." -ForegroundColor Cyan
git fetch origin

# Step 3: Try to merge the development branch into the feature branch
Write-Host "Attempting to merge development branch..." -ForegroundColor Cyan
git merge origin/development

# At this point, Git will likely report merge conflicts
# The conflicts will be in the files listed in the error message

Write-Host "`nIf you see merge conflicts, follow these steps to resolve them:" -ForegroundColor Yellow
Write-Host "1. Open each conflicting file in your editor" -ForegroundColor Yellow
Write-Host "2. Look for conflict markers (<<<<<<< HEAD, =======, >>>>>>> development)" -ForegroundColor Yellow
Write-Host "3. For each conflict:" -ForegroundColor Yellow
Write-Host "   - Keep your package changes (package org.jabberpoint.src.XXX)" -ForegroundColor Yellow
Write-Host "   - Keep your import changes (updated imports)" -ForegroundColor Yellow
Write-Host "   - For other conflicts, decide which changes to keep" -ForegroundColor Yellow
Write-Host "4. After resolving all conflicts, run:" -ForegroundColor Yellow
Write-Host "   git add ." -ForegroundColor Green
Write-Host "   git commit -m 'Resolved merge conflicts'" -ForegroundColor Green
Write-Host "   git push origin feature/package-creation" -ForegroundColor Green

Write-Host "`nFor specific files, here's what to do:" -ForegroundColor Cyan
Write-Host "- src/main/java/org/jabberpoint/src/model/Slide.java:" -ForegroundColor White
Write-Host "  Keep your package declaration and imports, merge any other changes" -ForegroundColor White

Write-Host "- src/main/java/org/jabberpoint/src/model/TextItem.java:" -ForegroundColor White
Write-Host "  Keep your package declaration and imports, merge any other changes" -ForegroundColor White

Write-Host "- src/main/java/org/jabberpoint/src/io/XMLAccessor.java:" -ForegroundColor White
Write-Host "  Keep your package declaration and imports, merge any other changes" -ForegroundColor White

Write-Host "- src/test/java/org/jabberpoint/test/BitmapItemTest.java:" -ForegroundColor White
Write-Host "  Keep your import changes, merge any other changes" -ForegroundColor White

Write-Host "- src/test/java/org/jabberpoint/test/SlideTest.java:" -ForegroundColor White
Write-Host "  Keep your import changes, merge any other changes" -ForegroundColor White

Write-Host "- src/test/java/org/jabberpoint/test/TextItemTest.java:" -ForegroundColor White
Write-Host "  Keep your import changes, merge any other changes" -ForegroundColor White

Write-Host "- src/test/java/org/jabberpoint/test/XMLPresentationLoaderTest.java:" -ForegroundColor White
Write-Host "  Keep your import changes, merge any other changes" -ForegroundColor White
