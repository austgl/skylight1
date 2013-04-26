while(<>) {
    chomp;
    $old = $_;
    $a = lc ($_);
    $a =~ s/-/_/g;
    if($a =~ /inactive/) {
	$a =~ s/inactive/active/g;
    } else {
        $a =~ s/active/inactive/g;
    }
    print "cp hires-art-assets/$old res/drawable-hdpi/$a\n";
}
